package de.uulm.in.vs.grn.chat.client.view;

import de.uulm.in.vs.grn.chat.client.Client;
import de.uulm.in.vs.grn.chat.client.connection.ConnectionEventListener;
import de.uulm.in.vs.grn.chat.client.connection.MTag;
import de.uulm.in.vs.grn.chat.client.connection.ServerConnector;
import de.uulm.in.vs.grn.chat.client.connection.events.MessageEvent;
import de.uulm.in.vs.grn.chat.client.connection.events.UserlistUpdateEvent;
import de.uulm.in.vs.grn.chat.client.connection.exceptions.ConnectionException;
import de.uulm.in.vs.grn.chat.client.connection.exceptions.MessageFormatException;
import javafx.event.ActionEvent;

import java.util.EventObject;

/**
 * Created by lg18 on 21.12.2017.
 */
public class ChatViewController implements ConnectionEventListener {
    public ChatView view;

    private ServerConnector connector;

    public ChatViewController() {
        connector = Client.getConnector();
        connector.regsisterListener(this);
    }

    public void handleSendMessage(String text) {
        view.inputBox.setText("");
        try {
            connector.sendMessage(text);
            view.infoLabel.setText("Message sent");
        } catch (MessageFormatException e) {
            e.printStackTrace();
        } catch (ConnectionException e) {
            view.infoLabel.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void handleConnectPubSub(ActionEvent actionEvent) {
        try {
            connector.connectPubSub();
            view.infoLabel.setText("PubSubConnection established.");
        } catch (ConnectionException e) {
            view.infoLabel.setText("Error: " + e.getMessage());
        }
    }

    public void handleConnectCommand() {
        String usrName = "Kruemel";
        try {
            boolean successfull = connector.connectCommand(usrName);
            if(!successfull) {
                view.infoLabel.setText("Username already in use.");
            } else {
                view.infoLabel.setText("CommandConnection established with username " + usrName);
            }
        } catch (ConnectionException | MessageFormatException e) {
            view.infoLabel.setText("Error: " + e.getMessage());
        }

    }



    @Override
    public void onConnectionEvent(EventObject event) {
        if(event instanceof MessageEvent) {
            String text = ((MessageEvent) event).getMsg().getTagContent(MTag.Text);
            view.chatBox.appendText(text + "\n");
        }
        if(event instanceof UserlistUpdateEvent) {
            String users = ((UserlistUpdateEvent) event).getMsg().getTagContent(MTag.Usernames);
        }
    }
}
