package de.uulm.in.vs.grn.chat.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.util.regex.Pattern;

/**
 * Created by lg18 on 21.12.2017.
 */
public class PubSubConnection implements AutoCloseable{
    private final String protocolVersion = "GRNCP/0.1";
    private final InetAddress serverHost;
    private final int serverPort;

    Socket connection;
    BufferedReader reader;

    public PubSubConnection(InetAddress serverHost, int serverPort) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    public void connect() throws IOException {
        connection = new Socket(serverHost,serverPort);
        reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
    }

    public void disconnect() throws IOException {
        connection.close();
        connection = null;
        reader.close();
        reader = null;
    }

    public Message readMessage() throws IOException, MessageFormatException {
        String line;
        Message msg;
        line = reader.readLine();
        if(line != null) {
            if(!line.startsWith(protocolVersion)) {
                throw new MessageFormatException("Protokoll Version not supported.");
            } else {
                String typeString = line.substring(protocolVersion.length() + 1);
                try {
                    MessageType mType = MessageType.valueOf(typeString);
                    msg = Message.newMessage(mType);
                } catch(IllegalArgumentException e) {
                    throw new MessageFormatException("Unknown Message Type: " + typeString);
                }
            }
        }
        while((line = reader.readLine()) != null) {
            Pattern p = Pattern.compile();

        }
    }

    public boolean isConnected() {
        if(connection == null || connection.isClosed()) return false;
        else return true;
    }

    @Override
    public void close() throws Exception {
        connection.close();
        reader.close();
    }
}
