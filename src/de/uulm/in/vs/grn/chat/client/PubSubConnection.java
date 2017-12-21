package de.uulm.in.vs.grn.chat.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.util.regex.Matcher;

/**
 * Created by lg18 on 21.12.2017.
 */
public class PubSubConnection implements AutoCloseable {

    private final InetAddress serverHost;
    private final int serverPort;

    Socket connection;
    BufferedReader reader;

    public PubSubConnection(InetAddress serverHost, int serverPort) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    public void connect() throws IOException {
        connection = new Socket(serverHost, serverPort);
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
            while ((line = reader.readLine()) != null) {
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
        throw new MessageFormatException(ErrorPriority.ERROR,"First line of the Message was empty.");
    }

    public boolean isConnected() {
        if (connection == null || connection.isClosed()) return false;
        else return true;
    }

    @Override
    public void close() throws Exception {
        connection.close();
        reader.close();
    }
}
