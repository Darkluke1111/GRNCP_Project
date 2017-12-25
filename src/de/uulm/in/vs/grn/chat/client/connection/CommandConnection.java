package de.uulm.in.vs.grn.chat.client.connection;

import de.uulm.in.vs.grn.chat.client.ErrorPriority;
import de.uulm.in.vs.grn.chat.client.connection.events.UserlistUpdateEvent;
import de.uulm.in.vs.grn.chat.client.connection.exceptions.ConnectionException;
import de.uulm.in.vs.grn.chat.client.connection.exceptions.MessageFormatException;

import java.net.InetAddress;
import java.util.Timer;
import java.util.TimerTask;

class CommandConnection extends Connection {

    private Timer pingTimer;

    public CommandConnection(InetAddress serverHost, int serverPort, ServerConnector connector) {
        super(serverHost, serverPort, connector);
    }

    public Message login(String usrName) throws ConnectionException, MessageFormatException {

        Message msg = Message.buildLoginMessage(usrName);

        writeMessage(msg);

        Message response = readMessage();

        if (response.getType() != MessageType.LOGGEDIN || response.getType() != MessageType.ERROR)
            throw new MessageFormatException(ErrorPriority.ERROR, "Expected LOGGEDIN or ERROR Message but got " + response.getType());
        return response;

    }

    public void logout() throws MessageFormatException, ConnectionException {
        Message msg = Message.buildByeMessage();
        writeMessage(msg);
        pingTimer.cancel();
        pingTimer.purge();
        Message response = readMessage();

        if (response.getType() != MessageType.BYEBYE) {
            throw new MessageFormatException(ErrorPriority.ERROR, "Expected BYEBYE Message but got " + response.getType());
        }

    }

    public boolean send(String text) throws MessageFormatException, ConnectionException {

        writeMessage(Message.buildSendMessage(text));

        Message response = readMessage();

        if (response.getType() == MessageType.ERROR) {
            return true;
        }
        if (response.getType() == MessageType.SENT) {
            return true;
        }
        throw new MessageFormatException(ErrorPriority.ERROR, "Expected ERROR or MESSAGE type but got " + response.getType());

    }

    public Message ping() throws MessageFormatException, ConnectionException {
        Message msg = Message.buildPingMessage();
        writeMessage(msg);

        Message response = readMessage();
        if (response.getType() != MessageType.PONG) {
            throw new MessageFormatException(ErrorPriority.ERROR, "Expected PONG Message but got " + response.getType());
        }
        return response;
    }

    public void startPeriodicPing(int period) {
        pingTimer = new Timer();
        pingTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    if (!isConnected()) {
                        pingTimer.cancel();
                        pingTimer.purge();
                    } else {
                        Message response = ping();
                        response.getTagContent(MTag.Usernames);
                        UserlistUpdateEvent event = new UserlistUpdateEvent(this, response);
                        connector.spreadEvent(event);
                    }
                } catch (MessageFormatException e) {
                    e.printStackTrace();
                } catch (ConnectionException e) {
                    e.printStackTrace();
                }
            }
        }, period * 1000, period * 1000);
    }
}
