package de.uulm.in.vs.grn.chat.client.view;

import de.uulm.in.vs.grn.chat.client.Connection;
import de.uulm.in.vs.grn.chat.client.ProtocolConstants;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import javafx.event.ActionEvent;

/**
 * Created by lg18 on 21.12.2017.
 */
public class ChatViewController {
    public ChatView view;

    public void handleSendMessage(String text) {
        view.inputBox.setText("");
        System.out.println(text);
    }

    public void handleConnectPubSub(ActionEvent actionEvent) {
        Connection connection = null;
        try {
            connection = new Connection(InetAddress
                .getByName(ProtocolConstants.STANDARD_HOST), ProtocolConstants.STANDARD_PS_PORT);

            connection.connect();
            view.infoLabel.setText(
                "Successfully connected to Pub/Sub ("
                    + ProtocolConstants.STANDARD_HOST
                    + ":"
                    + ProtocolConstants.STANDARD_PS_PORT);

        } catch (IOException e) {
            e.printStackTrace();
            view.infoLabel.setText("Error: " + e.getMessage());
        }
    }

    public void handleConnectPubSub() {
        Connection connection = null;

        try {
            connection = new Connection(InetAddress
                .getByName(ProtocolConstants.STANDARD_HOST), ProtocolConstants.STANDARD_COMMAND_PORT);

            connection.connect();


        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
