package de.uulm.in.vs.grn.chat.client.view;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

/**
 * Created by lg18 on 21.12.2017.
 */
public class ChatView {

    public Scene scene;
    public TextArea chatBox;
    public TextField inputBox;
    public Label infoLabel;

    private BorderPane root;
    private GridPane chatLayout;
    private HBox inputLine;

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



        chatBox = new TextArea();
        chatBox.setEditable(false);
        chatBox.setWrapText(true);
        chatBox.prefHeightProperty().bind(chatLayout.heightProperty());
        chatBox.prefWidthProperty().bind(chatLayout.widthProperty());

        send = new Button("Send");
        inputBox = new TextField();
        inputBox.prefWidthProperty().bind(chatLayout.widthProperty().subtract(send.widthProperty()));
        inputLine = new HBox();
        inputLine.getChildren().addAll(inputBox,send);


        chatLayout.add(chatBox,1,1);
        chatLayout.add(inputLine,1,2);
        root.setCenter(chatLayout);

        options = new Menu("Options");
        menuBar = new MenuBar();
        menuBar.getMenus().add(options);
        root.setTop(menuBar);

        infoLabel = new Label("Started Client");
        infoLabel.setPadding(new Insets(2,2,2,2));
        root.setBottom(infoLabel);



    }


    public void connectHandlers(ChatViewController c) {
        c.view = this;

        inputBox.setOnKeyPressed( e -> {
            if(e.getCode() == KeyCode.ENTER) {
                c.handleSendMessage(inputBox.getText());
            }
        });

        send.setOnAction(e -> c.handleSendMessage(inputBox.getText()));
    }
}
