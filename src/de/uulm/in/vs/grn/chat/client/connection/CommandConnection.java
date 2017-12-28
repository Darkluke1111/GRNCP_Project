package de.uulm.in.vs.grn.chat.client.connection;

import de.uulm.in.vs.grn.chat.client.ErrorPriority;
import de.uulm.in.vs.grn.chat.client.connection.events.ConnectionExpiredEvent;
import de.uulm.in.vs.grn.chat.client.connection.events.UserlistUpdateEvent;
import de.uulm.in.vs.grn.chat.client.connection.exceptions.ConnectionException;
import de.uulm.in.vs.grn.chat.client.connection.exceptions.MessageFormatException;
import java.net.InetAddress;
import java.util.Timer;
import java.util.TimerTask;

class CommandConnection extends Connection {

  private Timer pingTimer;
  private int pingPeriod;
  private boolean loggedin;

  public CommandConnection(InetAddress serverHost, int serverPort, ServerConnector connector) {
    super(serverHost, serverPort, connector);
    pingPeriod = 60;
    loggedin = false;
  }

  public synchronized boolean login(String usrName) throws ConnectionException, MessageFormatException {
    //Check whether the Connection is still up
    if (!isConnected()) {
      throw new ConnectionException("Not connected to CommandServer", null);
    }
    //Send Login Message
    writeMessage(Message.buildLoginMessage(usrName));
    //Read Response
    Message response = readMessage();
    //Handle Response
    switch (response.getType()) {
      case ERROR:
        return false;
      case LOGGEDIN:
        loggedin = true;
        startPeriodicPing();
        return true;
      default:
        throw new MessageFormatException(ErrorPriority.ERROR,
            "Expected LOGGEDIN response but got "
                + response.getType());
    }
  }

  public synchronized void logout()
      throws MessageFormatException, ConnectionException {
    //Check whether the Connection is still up
    if (!isConnected() || loggedin) {
      return;
    }
    //Write logout Message
    writeMessage(Message.buildByeMessage());
    //read response
    Message response = readMessage();
    //Handle Response
    switch (response.getType()) {
      case BYEBYE:
        loggedin = false;
        stopPeriodicPing();
        break;
      case EXPIRED:
        loggedin = false;
        stopPeriodicPing();
        break;
      default:
        throw new MessageFormatException(ErrorPriority.ERROR,
            "Expected BYEBYE response but got "
                + response.getType());
    }
  }

  public synchronized boolean send(String text)
      throws MessageFormatException, ConnectionException {
    //Write send Message
    writeMessage(Message.buildSendMessage(text));
    //Read Response
    Message response = readMessage();
    //Handle Response
    switch (response.getType()) {
      case SENT:
        return true;
      case ERROR:
        return false;
      case EXPIRED:
        loggedin = false;
        stopPeriodicPing();
        connector.spreadEvent(new ConnectionExpiredEvent(this));
        return false;
      default:
        throw new MessageFormatException(ErrorPriority.ERROR,
            "Expected SENT response but got "
                + response.getType());
    }
  }

  public synchronized Message ping()
      throws MessageFormatException, ConnectionException {

    //Write ping Message
    writeMessage(Message.buildPingMessage());

    //Read Response
    Message response = readMessage();

    //Handle Response
    switch (response.getType()) {
      case PONG:
        return response;
      case EXPIRED:
        loggedin = false;
        stopPeriodicPing();
        connector.spreadEvent(new ConnectionExpiredEvent(this));
      default:
        throw new MessageFormatException(ErrorPriority.ERROR,
            "Expected SENT response but got "
                + response.getType());
    }
  }

  private void stopPeriodicPing() {
    if(pingTimer != null) pingTimer.cancel();
  }

  private void startPeriodicPing() {
    //Cancel a time which is already running
    if(pingTimer != null) {
      pingTimer.cancel();
    }
    // Start a new Timer
    pingTimer = new Timer();
    pingTimer.scheduleAtFixedRate(new TimerTask() {
      @Override
      public void run() {
        try {
          // Stop the Timer if the connection is down or the user isn't logged in
          if (!isConnected() || !loggedin) {
            pingTimer.cancel();
            pingTimer.purge();
          } else {
            //Stop execution if the thread was interrupted
            if(Thread.interrupted()) return;

            // Send Ping Message
            Message response = ping();
            response.getTagContent(MTag.Usernames);

            // Send an UserlistUpdateEvent to the EventHandler
            UserlistUpdateEvent event = new UserlistUpdateEvent(this, response);
            connector.spreadEvent(event);
          }
        } catch (MessageFormatException | ConnectionException e) {
          //ignore
        }
      }
    }, 0, pingPeriod * 1000);
  }
}
