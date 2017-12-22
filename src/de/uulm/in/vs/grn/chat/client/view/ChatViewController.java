package de.uulm.in.vs.grn.chat.client.view;

import de.uulm.in.vs.grn.chat.client.ProtocolConstants;
import de.uulm.in.vs.grn.chat.client.PubSubConnection;
import java.io.IOException;
import java.net.InetAddress;
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
        PubSubConnection connection = null;
        try {
            connection = new PubSubConnection(InetAddress.getByName(ProtocolConstants.STANDARD_HOST), ProtocolConstants.STANDARD_PS_PORT);
            connection.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
