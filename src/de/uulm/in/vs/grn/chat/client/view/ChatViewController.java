package de.uulm.in.vs.grn.chat.client.view;

import de.uulm.in.vs.grn.chat.client.Client;
import de.uulm.in.vs.grn.chat.client.connection.ConnectionEventListener;
import de.uulm.in.vs.grn.chat.client.connection.MTag;
import de.uulm.in.vs.grn.chat.client.connection.ServerConnector;
import de.uulm.in.vs.grn.chat.client.connection.events.ConnectionExpiredEvent;
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
    connector.registerListener(this);

  }

  public void handleSendMessage(String text) {
    view.inputBox.setText("");
    try {
      connector.sendMessage(text);
      view.infoLabel.setText("Message sent");
    } catch (MessageFormatException | ConnectionException e) {
      view.infoLabel.setText("Error: " + e.getMessage());
    }
  }

  public void handleConnect() {
    try {
      connector.connectCommand();
      login();

      connector.connectPubSub();
    } catch (ConnectionException | MessageFormatException e) {
      view.infoLabel.setText("Error: " + e.getMessage());
    }
  }

  public void handleDisconnect() {
    try {
      connector.disconnectPubSub();
      connector.logoutCommand();
      connector.disconnectCommand();
    } catch (MessageFormatException | ConnectionException e) {
      view.infoLabel.setText("Error: " + e.getMessage());
    }
  }

  @Override
  public void onConnectionEvent(EventObject event) {
  }

  @Override
  public void onJoinEvent(JoinEvent event) {
    Text eventText = new Text();
    eventText.setStyle("-fx-fill: RED;-fx-font-weight:normal;");
    eventText.setText(event.getMsg().getTagContent(MTag.Description) + "\n");
    Platform.runLater(() -> view.chatBox.getChildren().add(eventText));
  }

  @Override
  public void onMessageEvent(MessageEvent event) {
    String text = event.getMsg().getTagContent(MTag.Text);
    String name = event.getMsg().getTagContent(MTag.Username);

    Text userName = new Text();
    userName.setStyle("-fx-fill: #4F8A10;-fx-font-weight:bold;");
    userName.setText(name + ":");

    Text chat = new Text();
    chat.setText(text + "\n");
    Platform.runLater(() -> view.chatBox.getChildren().addAll(userName, chat));
  }

  @Override
  public void onUserlistUpdateEvent(UserlistUpdateEvent event) {
    String[] users = event.getMsg().getTagContent(MTag.Usernames).trim().split(",");
    Platform.runLater(() -> {
      view.userList.getItems().clear();
      view.userList.getItems().addAll(users);
    });
  }

  @Override
  public void onConnectionExpiredEvent(ConnectionExpiredEvent event) {

  }

  private void login() throws ConnectionException, MessageFormatException {
    while (true) {
      TextInputDialog nameInput = new TextInputDialog("Username");
      nameInput.setTitle("Username Selection");
      nameInput.setHeaderText("Select you Username");
      Optional<String> result = nameInput.showAndWait();
      if (!result.isPresent()) {
        continue;
      }
      String usrName = result.get();

      boolean successfull = connector.loginCommand(usrName);
      if (!successfull) {
        view.infoLabel.setText("Username already in use.");
        continue;
      } else {
        view.infoLabel.setText("Login successfull with username " + usrName);
        return;
      }
    }
  }
}
