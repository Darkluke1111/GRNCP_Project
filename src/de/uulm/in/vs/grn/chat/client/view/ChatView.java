package de.uulm.in.vs.grn.chat.client.view;

import de.uulm.in.vs.grn.chat.client.Client;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.TextFlow;

/**
 * Created by lg18 on 21.12.2017.
 */
public class ChatView {

  public Scene scene;
  public TextFlow chatBox;
  public TextField inputBox;
  public Label infoLabel;
  public ListView userList;

  private BorderPane root;
  private GridPane chatLayout;
  private HBox inputLine;
  private SplitPane splitter;
  private ScrollPane chatScroll;

  private MenuItem connect;
  private MenuItem disconnect;
  private Menu options;
  private MenuBar menuBar;
  private Button send;


  public ChatView() {
    createScene();
  }

  public void createScene() {
    root = new BorderPane();
    scene = new Scene(root);

    chatLayout = new GridPane();


    chatBox = new TextFlow();
    chatScroll = new ScrollPane();
    inputLine = new HBox();

    chatBox.prefHeightProperty().bind(chatLayout.heightProperty().subtract(inputLine.heightProperty()));
    chatBox.prefWidthProperty().bind(chatLayout.widthProperty().subtract(20));


    chatScroll.setContent(chatBox);

    send = new Button("Send");
    inputBox = new TextField();


    inputLine.getChildren().addAll(inputBox, send);


    chatLayout.add(chatScroll, 1, 1);
    chatLayout.add(inputLine, 1, 2);

    userList = new ListView();
    splitter = new SplitPane();

    splitter.getItems().addAll(userList, chatLayout);

    root.setCenter(splitter);

    disconnect = new MenuItem("Disconnect");
    connect = new MenuItem("Connect");
    options = new Menu("Options");
    options.getItems().addAll(connect, disconnect);
    menuBar = new MenuBar();
    menuBar.getMenus().add(options);
    root.setTop(menuBar);

    infoLabel = new Label("Started Client");
    infoLabel.setPadding(new Insets(2, 2, 2, 2));
    root.setBottom(infoLabel);


    Client.primaryStage.showingProperty().addListener(new ChangeListener<Boolean>() {

      @Override
      public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        if (newValue) {
          splitter.setDividerPositions(0.2);
          inputBox.prefWidthProperty().bind(chatLayout.widthProperty().subtract(send.widthProperty()));
          chatScroll.vvalueProperty().bind(chatBox.heightProperty());
          observable.removeListener(this);
        }
      }

    });


  }


  public void connectHandlers(ChatViewController c) {
    c.view = this;

    inputBox.setOnKeyPressed(e -> {
      if (e.getCode() == KeyCode.ENTER) {
        c.handleSendMessage(inputBox.getText());
      }
    });

    send.setOnAction(e -> c.handleSendMessage(inputBox.getText()));

    connect.setOnAction(e -> {
      c.handleConnect();
    });

    disconnect.setOnAction(e -> {
      c.handleDisconnect();
    });
  }
}
