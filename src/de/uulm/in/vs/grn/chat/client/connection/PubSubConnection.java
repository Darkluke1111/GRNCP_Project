package de.uulm.in.vs.grn.chat.client.connection;

import de.uulm.in.vs.grn.chat.client.connection.events.JoinEvent;
import de.uulm.in.vs.grn.chat.client.connection.events.MessageEvent;
import de.uulm.in.vs.grn.chat.client.connection.exceptions.ConnectionException;
import de.uulm.in.vs.grn.chat.client.connection.exceptions.MessageFormatException;
import java.net.InetAddress;
import java.util.EventObject;

public class PubSubConnection extends Connection{

  public PubSubConnection(InetAddress serverHost, int serverPort, ServerConnector connector) {
    super(serverHost, serverPort, connector);
  }

  public void waitForMessages() {
    new Thread(() -> {
      while(isConnected()) {
        try {
          Message msg = readMessage();
          EventObject event = null;
          if(msg.getType() == MessageType.MESSAGE) {
            event = new MessageEvent(this, msg);
          }
          if(msg.getType() == MessageType.EVENT) {
            event = new JoinEvent(this, msg);
          }
          if(event != null) {
            connector.spreadEvent(event);
          }
        } catch (MessageFormatException e) {

        } catch (ConnectionException e) {

        }
      }
    }).start();
  }
}
