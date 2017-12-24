package de.uulm.in.vs.grn.chat.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.util.regex.Matcher;

public class Connection implements AutoCloseable {
  protected final InetAddress serverHost;
  protected final int serverPort;

  protected Socket connection;
  protected BufferedReader reader;

  public Connection(InetAddress serverHost, int serverPort) {
    this.serverHost = serverHost;
    this.serverPort = serverPort;
  }

  public void disconnect() throws IOException {
    connection.close();
    connection = null;
    reader.close();
    reader = null;
  }


  public boolean isConnected() {
    if (connection == null || connection.isClosed()) {
      return false;
    } else {
      return true;
    }
  }

  @Override
  public void close() throws Exception {
    connection.close();
    reader.close();
  }

  public void connect() throws IOException {
    connection = new Socket(serverHost, serverPort);
    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
    new Thread(() -> {
      while (true) {
        try {
          Message msg = readMessage();
          if (msg.getType() == MessageType.MESSAGE) {
            System.out.println(msg.getTagContent(MTag.Username) + ": " + msg.getTagContent(MTag.Text));
          }
          if(msg.getType() == MessageType.EVENT) {
            System.out.println(msg.getTagContent(MTag.Description));
          }
        } catch (IOException e) {
          e.printStackTrace();
        } catch (MessageFormatException e) {
          e.printStackTrace();
        }

      }
    }).start();
  }

  public Message readMessage() throws IOException, MessageFormatException {
    String line;
    Message msg;

    //Read first line
    line = reader.readLine();

    if (line != null) {

      //Test for the right protocol version
      if (!line.startsWith(ProtocolConstants.VERSION)) {
        throw new MessageFormatException(ErrorPriority.ERROR, "Protokoll Version not supported.");
      }

      //Get the Type of the Message and create a new Message of that type
      String typeString = line.substring(ProtocolConstants.VERSION.length() + 1);
      try {
        MessageType mType = MessageType.valueOf(typeString);
        msg = Message.newMessage(mType);
      } catch (IllegalArgumentException e) {
        throw new MessageFormatException(ErrorPriority.ERROR, "Unknown Message Type: " + typeString);
      }

      //Read the rest of the Message and parse the lines into the right MessageTags
      while ((line = reader.readLine()) != null && !line.equals("")) {

        Matcher m = ProtocolConstants.TAG_LAYOUT.matcher(line);
        if (m.matches() && m.groupCount() == 2) {
          try {
            MTag tag = MTag.valueOf(m.group(1));
            msg.withTag(tag, m.group(2));
          } catch (IllegalArgumentException e) {
            Logger.logWarning("Detected unrecognized Tag: '" + m.group(1) + "', ignoring it.");
          }
        }
      }

      //Validate the Message
      msg.validate();
      return msg;
    }
    throw new MessageFormatException(ErrorPriority.ERROR, "First line of the Message was empty.");
  }

  public void sendMessage(Message msg) {
    StringBuilder sb = new StringBuilder();

    //Zeile 1:
    sb.append(msg.getType().toString());
    sb.append(" ");
    sb.append(ProtocolConstants.VERSION);
    sb.append("\r\n");

    //Tags:
    for (MTag tag : MTag.values()) {
      if (msg.hasTag(tag)) {
        sb.append(tag.toString());
        sb.append(": ");
        sb.append(msg.getTagContent(tag));
        sb.append("\r\n");
      }
    }
    sb.append("\r\n");
    System.out.println();
  }
}
