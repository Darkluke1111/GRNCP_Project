package de.uulm.in.vs.grn.chat.client;

import java.io.IOException;
import java.net.InetAddress;

public class CommandConnection extends Connection {

  public CommandConnection(InetAddress serverHost, int serverPort) {
    super(serverHost, serverPort);
  }

  public boolean login(String usrName) throws MessageFormatException {
    Message msg = Message.buildLoginMessage(usrName);
  }
}
