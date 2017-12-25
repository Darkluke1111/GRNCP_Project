package de.uulm.in.vs.grn.chat.client.connection;

import java.net.InetAddress;

public class ServerConnector {
    private InetAddress serverAddress;
    private int pubSubPort;
    private int commandPort;

    private static final int pingDurationSeconds = 60;

    private Connection pubSubConnection;
    private CommandConnection commandConnection;

    public ServerConnector(InetAddress serverAddress, int pubSubPort, int commandPort){
        this.serverAddress = serverAddress;
        this.pubSubPort = pubSubPort;
        this.commandPort = commandPort;

        pubSubConnection = new Connection(serverAddress,pubSubPort);
        commandConnection = new CommandConnection(serverAddress,commandPort);
    }

    public void connectPubSub() throws ConnectionException {
        if(!pubSubConnection.isConnected())
            pubSubConnection.connect();
    }

    public boolean connectCommand(String usrName) throws ConnectionException, MessageFormatException {
        if(!commandConnection.isConnected())
            commandConnection.connect();

         Message response = commandConnection.login(usrName);
        if(response.getType() == MessageType.ERROR) {
            return false;
        } else {
            commandConnection.startPeriodicPing(pingDurationSeconds);
            return true;
        }
    }

    public void disconnectPubSub() {
        pubSubConnection.disconnect();
        try {
            pubSubConnection.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void disconnectCommand() throws MessageFormatException, ConnectionException {
        commandConnection.logout();
        commandConnection.disconnect();
        try {
            pubSubConnection.close();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public void sendMessage(String text) throws MessageFormatException, ConnectionException {
        commandConnection.sendMessage(Message.buildSendMessage(text));
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
}
