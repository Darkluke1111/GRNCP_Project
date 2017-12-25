package de.uulm.in.vs.grn.chat.client.connection;

import de.uulm.in.vs.grn.chat.client.ErrorPriority;

import java.io.IOException;
import java.net.InetAddress;

public class CommandConnection extends Connection {

  public CommandConnection(InetAddress serverHost, int serverPort) {
    super(serverHost, serverPort);
  }

  public void login(String usrName) throws MessageFormatException, IOException, LoginRefusedException {
    Message msg = Message.buildLoginMessage(usrName);
    writer.write(msg.toSendFormat());

    Message response = readMessage();
    if(response.getType() == MessageType.ERROR)
      throw new LoginRefusedException("Login was refused by Server.");
    if(response.getType() != MessageType.LOGGEDIN)
      throw new MessageFormatException(ErrorPriority.ERROR, "Expected LOGGEDIN Message but got " + response.getType());
  }

  public void logout() throws MessageFormatException, IOException {
    Message msg = Message.buildByeMessage();
    writer.write(msg.toSendFormat());

    Message response = readMessage();
    disconnect();
    if(response.getType() != MessageType.BYEBYE) {
      throw new MessageFormatException(ErrorPriority.ERROR, "Expected BYEBYE MEssage but got " + response.getType());
    }
  }

  public Message ping() throws MessageFormatException, IOException {
    Message msg = Message.buildPingMessage();
    writer.write(msg.toSendFormat());

    Message response = readMessage();
    if(response.getType() != MessageType.PONG) {
      throw  new MessageFormatException(ErrorPriority.ERROR, "Expected PONG Message but got " + response.getType());
    }
    return response;
  }
}
