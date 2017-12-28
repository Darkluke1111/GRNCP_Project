package de.uulm.in.vs.grn.chat.client.connection;

import de.uulm.in.vs.grn.chat.client.*;
import de.uulm.in.vs.grn.chat.client.connection.events.JoinEvent;
import de.uulm.in.vs.grn.chat.client.connection.events.MessageEvent;
import de.uulm.in.vs.grn.chat.client.connection.exceptions.ConnectionException;
import de.uulm.in.vs.grn.chat.client.connection.exceptions.MessageFormatException;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.EventObject;
import java.util.regex.Matcher;

abstract class Connection implements AutoCloseable {
    protected final InetAddress serverHost;
    protected final int serverPort;

    protected Socket connection;
    protected BufferedReader reader;
    protected BufferedWriter writer;

    protected EventHandler connector;

    public Connection(InetAddress serverHost, int serverPort, EventHandler connector) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.connector = connector;
    }

    public synchronized void disconnect() {
        if(!isConnected()) return;
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

    public synchronized void connect() throws ConnectionException {
        try {
            connection = new Socket(serverHost, serverPort);

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
        } catch (IOException e) {
            throw new ConnectionException("Wasn't able to establish Connection to Server", e);
        }
    }

    public synchronized Message readMessage() throws MessageFormatException, ConnectionException {
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

    public synchronized void writeMessage(Message msg) throws ConnectionException {
            String msgString = msg.toString();
        try {
            writer.write(msgString);
            writer.flush();
        } catch(IOException e) {
            throw new ConnectionException("Wasn't able to access socket outputstream, was the connection reset?", e);
        }
    }
}
