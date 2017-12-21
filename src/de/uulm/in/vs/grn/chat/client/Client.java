package de.uulm.in.vs.grn.chat.client;

import de.uulm.in.vs.grn.chat.client.view.ChatView;
import de.uulm.in.vs.grn.chat.client.view.ChatViewController;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Created by lg18 on 21.12.2017.
 */
public class Client extends Application{

    @Override
    public void start(Stage primaryStage) throws Exception {
        ChatView view = new ChatView();
        ChatViewController c = new ChatViewController();
        view.connectHandlers(c);
        primaryStage.setScene(view.scene);
        primaryStage.setHeight(600);
        primaryStage.setWidth(600);
        primaryStage.show();
    }

    public static void main(String args[]) {
        launch();
    }
}
