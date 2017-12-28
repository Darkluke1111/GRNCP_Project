package de.uulm.in.vs.grn.chat.client.view;

import de.uulm.in.vs.grn.chat.client.Client;
import de.uulm.in.vs.grn.chat.client.connection.ConnectionEventListener;
import de.uulm.in.vs.grn.chat.client.connection.MTag;
import de.uulm.in.vs.grn.chat.client.connection.ServerConnector;
import de.uulm.in.vs.grn.chat.client.connection.events.JoinEvent;
import de.uulm.in.vs.grn.chat.client.connection.events.MessageEvent;
import de.uulm.in.vs.grn.chat.client.connection.events.UserlistUpdateEvent;
import de.uulm.in.vs.grn.chat.client.connection.exceptions.ConnectionException;
import de.uulm.in.vs.grn.chat.client.connection.exceptions.MessageFormatException;
import java.util.EventObject;
import java.util.Optional;
import javafx.application.Platform;
import javafx.scene.control.TextInputDialog;
import javafx.scene.text.Text;

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
      view.infoLabel.setText("Error: " + e.getMessage());
    } catch (ConnectionException e) {
      view.infoLabel.setText("Error: " + e.getMessage());
    }
  }

  public void handleConnectPubSub() {
    try {
      connector.connectPubSub();
      view.infoLabel.setText("PubSubConnection established.");
    } catch (ConnectionException e) {
      view.infoLabel.setText("Error: " + e.getMessage());
    }
  }

  public void handleConnectCommand() {
    TextInputDialog nameInput = new TextInputDialog("Username");
    nameInput.setTitle("Username Selection");
    nameInput.setHeaderText("Select you Username");

    Optional<String> result = nameInput.showAndWait();
    if (!result.isPresent()) {
      return;
    } else {
      String usrName = result.get();

      try {
        boolean successfull = connector.connectCommand(usrName);
        if (!successfull) {
          view.infoLabel.setText("Username already in use.");
        } else {
          view.infoLabel.setText("CommandConnection established with username " + usrName);
        }
      } catch (ConnectionException | MessageFormatException e) {
        view.infoLabel.setText("Error: " + e.getMessage());
      }
    }

  }

  public void handleDisconnectCommand() {
    try {
      connector.disconnectCommand();
    } catch (MessageFormatException | ConnectionException e) {
      view.infoLabel.setText("Error: " + e.getMessage());
    }
  }

  public void handleDisconnectPubSub() {
    connector.disconnectPubSub();
  }


  @Override
  public void onConnectionEvent(EventObject event) {
    if (event instanceof MessageEvent) {
      MessageEvent me = ((MessageEvent) event);
      String text = me.getMsg().getTagContent(MTag.Text);
      String name = me.getMsg().getTagContent(MTag.Username);

      Text userName = new Text();
      userName.setStyle("-fx-fill: #4F8A10;-fx-font-weight:bold;");
      userName.setText(name + ":");

      Text chat = new Text();
      chat.setText(text + "\n");
      Platform.runLater(() -> view.chatBox.getChildren().addAll(userName, chat));
    }

    if (event instanceof JoinEvent) {
      JoinEvent je = (JoinEvent) event;
      Text eventText = new Text();
      eventText.setStyle("-fx-fill: RED;-fx-font-weight:normal;");
      eventText.setText(je.getMsg().getTagContent(MTag.Description) + "\n");
      Platform.runLater(() -> view.chatBox.getChildren().add(eventText));
    }
    if (event instanceof UserlistUpdateEvent) {
      String[] users = ((UserlistUpdateEvent) event).getMsg().getTagContent(MTag.Usernames).trim().split(",");
      Platform.runLater(() -> {
        view.userList.getItems().clear();
        view.userList.getItems().addAll(users);
      });

    }
  }
}
