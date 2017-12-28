package de.uulm.in.vs.grn.chat.client.connection;

import de.uulm.in.vs.grn.chat.client.ErrorPriority;
import de.uulm.in.vs.grn.chat.client.connection.exceptions.ConnectionException;
import de.uulm.in.vs.grn.chat.client.connection.exceptions.MessageFormatException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

public class ServerConnector implements AutoCloseable, EventHandler {
  private InetAddress serverAddress;
  private int pubSubPort;
  private int commandPort;

  private static final int pingDurationSeconds = 60;

  private PubSubConnection pubSubConnection;
  private CommandConnection commandConnection;

  private List<ConnectionEventListener> listenerList;

  public ServerConnector(InetAddress serverAddress, int pubSubPort, int commandPort) {
    this.serverAddress = serverAddress;
    this.pubSubPort = pubSubPort;
    this.commandPort = commandPort;

    listenerList = new ArrayList<>();

    pubSubConnection = new PubSubConnection(serverAddress, pubSubPort, this);
    commandConnection = new CommandConnection(serverAddress, commandPort, this);
  }

  public void connectPubSub() throws ConnectionException {
    if (!pubSubConnection.isConnected()) {
      pubSubConnection.connect();
      pubSubConnection.waitForMessages();
    }
  }

  public boolean connectCommand(String usrName) throws ConnectionException, MessageFormatException {
    if (!commandConnection.isConnected()) {
      commandConnection.connect();
    }

    Message response = commandConnection.login(usrName);

    switch(response.getType()) {
      case ERROR:
        return false;
      case LOGGEDIN:
        return true;
      default:
        throw new MessageFormatException(ErrorPriority.ERROR,
            "Expected LOGGEDIN response but got "
                + response.getType());
    }
  }

  public void disconnectPubSub() {
    pubSubConnection.disconnect();
    try {
      pubSubConnection.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void disconnectCommand() throws MessageFormatException, ConnectionException {
    commandConnection.logout();
    commandConnection.disconnect();
    try {
      pubSubConnection.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void sendMessage(String text) throws MessageFormatException, ConnectionException {

    commandConnection.send(text);
  }

  @Override
  public void registerListener(ConnectionEventListener listener) {
    listenerList.add(listener);
  }


  @Override
  public void spreadEvent(EventObject event) {
    for (ConnectionEventListener listener : listenerList) {
      listener.onConnectionEvent(event);
    }
  }

  public InetAddress getServerAddress() {
    return serverAddress;
  }

  public int getPubSubPort() {
    return pubSubPort;
  }

  public int getCommandPort() {
    return commandPort;
  }

  @Override
  public void close() throws Exception {
    disconnectPubSub();
    disconnectCommand();
    listenerList = null;
  }
}
