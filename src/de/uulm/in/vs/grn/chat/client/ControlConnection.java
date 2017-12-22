package de.uulm.in.vs.grn.chat.client;

import java.net.InetAddress;

public class ControlConnection extends Connection{

  public ControlConnection(InetAddress serverHost, int serverPort) {
    super(serverHost,serverPort);
  }

}
