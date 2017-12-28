package de.uulm.in.vs.grn.chat.client.connection;

import de.uulm.in.vs.grn.chat.client.connection.events.JoinEvent;
import de.uulm.in.vs.grn.chat.client.connection.events.MessageEvent;
import de.uulm.in.vs.grn.chat.client.connection.exceptions.ConnectionException;
import de.uulm.in.vs.grn.chat.client.connection.exceptions.MessageFormatException;
import java.net.InetAddress;
import java.util.EventObject;

public class PubSubConnection extends Connection {

  public PubSubConnection(InetAddress serverHost, int serverPort, ServerConnector connector) {
    super(serverHost, serverPort, connector);
  }

  @Override
  public synchronized void connect() throws ConnectionException {
    super.connect();
    waitForMessages();
  }

  private void waitForMessages() {
    new Thread(() -> {
      while (isConnected()) {
        Message msg = null;
        try {
          msg = readMessage();

        EventObject event = null;
          switch (msg.getType()) {
            case MESSAGE:
              event = new MessageEvent(this, msg);
              break;
            case EVENT:
              event = new JoinEvent(this, msg);
              break;
            default:
          }
          connector.spreadEvent(event);
        } catch (MessageFormatException | ConnectionException e) {
          //ignore
        }
      }
      }).start();
    }
  }
