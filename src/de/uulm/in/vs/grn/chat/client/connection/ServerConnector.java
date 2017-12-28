package de.uulm.in.vs.grn.chat.client.connection;

import de.uulm.in.vs.grn.chat.client.connection.events.ConnectionExpiredEvent;
import de.uulm.in.vs.grn.chat.client.connection.events.JoinEvent;
import de.uulm.in.vs.grn.chat.client.connection.events.MessageEvent;
import de.uulm.in.vs.grn.chat.client.connection.events.UserlistUpdateEvent;
import de.uulm.in.vs.grn.chat.client.connection.exceptions.ConnectionException;
import de.uulm.in.vs.grn.chat.client.connection.exceptions.MessageFormatException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

public class ServerConnector implements AutoCloseable, EventHandler {


  private PubSubConnection pubSubConnection;
  private CommandConnection commandConnection;

  private List<ConnectionEventListener> listenerList;

  public ServerConnector(InetAddress serverAddress, int pubSubPort, int commandPort) {

    listenerList = new ArrayList<>();

    pubSubConnection = new PubSubConnection(serverAddress, pubSubPort, this);
    commandConnection = new CommandConnection(serverAddress, commandPort, this);
  }

  public void connectPubSub() throws ConnectionException {
      pubSubConnection.connect();
  }

  public void connectCommand() throws ConnectionException {
    commandConnection.connect();
  }

  public boolean loginCommand(String usrName)
      throws ConnectionException, MessageFormatException {
    return commandConnection.login(usrName);
  }

  public void disconnectPubSub() {
    pubSubConnection.disconnect();
  }

  public void logoutCommand()
      throws MessageFormatException, ConnectionException {
    commandConnection.logout();
  }

  public void disconnectCommand()
      throws MessageFormatException, ConnectionException {
    commandConnection.disconnect();
  }

  public void sendMessage(String text)
      throws MessageFormatException, ConnectionException {
    commandConnection.send(text);
  }

  @Override
  public void registerListener(ConnectionEventListener listener) {
    listenerList.add(listener);
  }


  @Override
  public synchronized void spreadEvent(EventObject event) {
    for (ConnectionEventListener listener : listenerList) {
      listener.onConnectionEvent(event);
      if(event instanceof JoinEvent) {
        listener.onJoinEvent((JoinEvent) event);
      }
      if(event instanceof MessageEvent) {
        listener.onMessageEvent((MessageEvent) event);
      }
      if(event instanceof UserlistUpdateEvent) {
        listener.onUserlistUpdateEvent((UserlistUpdateEvent) event);
      }
      if(event instanceof ConnectionExpiredEvent) {
        listener.onConnectionExpiredEvent((ConnectionExpiredEvent) event);
      }
    }
  }

  @Override
  public void close() throws Exception {
    disconnectPubSub();
    logoutCommand();
    disconnectCommand();
    listenerList = null;
  }
}
