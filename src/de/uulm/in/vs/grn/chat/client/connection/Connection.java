package de.uulm.in.vs.grn.chat.client.connection;

import de.uulm.in.vs.grn.chat.client.*;
import de.uulm.in.vs.grn.chat.client.connection.events.MessageEvent;
import de.uulm.in.vs.grn.chat.client.connection.exceptions.ConnectionException;
import de.uulm.in.vs.grn.chat.client.connection.exceptions.MessageFormatException;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.regex.Matcher;

class Connection implements AutoCloseable {
    protected final InetAddress serverHost;
    protected final int serverPort;

    protected Socket connection;
    protected BufferedReader reader;
    protected BufferedWriter writer;

    protected ServerConnector connector;

    public Connection(InetAddress serverHost, int serverPort, ServerConnector connector) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.connector = connector;
    }

    public void disconnect() {
        try {
            connection.close();
            reader.close();
            writer.close();
        } catch(IOException e){
            e.printStackTrace();
        }
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
        writer.close();
    }

    public void connect() throws ConnectionException {
        try {
            connection = new Socket(serverHost, serverPort);

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
        } catch (IOException e) {
            throw new ConnectionException("Wasn't able to establish Connection to Server", e);
        }
    }

    public Message readMessage() throws MessageFormatException, ConnectionException {
        String line;
        Message msg;
        try {
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
        } catch(IOException e) {
            throw new ConnectionException("Wasn't able to access socket inputstream. Was the connection reset?", e);
        }
        throw new MessageFormatException(ErrorPriority.ERROR, "First line of the Message was empty.");
    }

    public void writeMessage(Message msg) throws ConnectionException {
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
        try {
            writer.write(sb.toString());
        } catch(IOException e) {
            throw new ConnectionException("Wasn't able to access socket outputstream, was the connection reset?", e);
        }
    }

    public void waitForMessages() {
        new Thread(() -> {
           while(isConnected()) {
               try {
                   Message msg = readMessage();
                   MessageEvent event = new MessageEvent(this,msg);
                   connector.spreadEvent(event);
               } catch (MessageFormatException e) {
                   e.printStackTrace();
               } catch (ConnectionException e) {
                   e.printStackTrace();
               }
           }
        });
    }
}
