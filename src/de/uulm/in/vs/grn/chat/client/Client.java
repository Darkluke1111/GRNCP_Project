package de.uulm.in.vs.grn.chat.client;

import de.uulm.in.vs.grn.chat.client.connection.ProtocolConstants;
import de.uulm.in.vs.grn.chat.client.connection.ServerConnector;
import de.uulm.in.vs.grn.chat.client.view.ChatView;
import de.uulm.in.vs.grn.chat.client.view.ChatViewController;
import javafx.application.Application;
import javafx.stage.Stage;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by lg18 on 21.12.2017.
 */
public class Client extends Application{

    private static ServerConnector connector;

    public static void main(String args[]) {
        try {
            connector = new ServerConnector(
                    InetAddress.getByName(ProtocolConstants.STANDARD_HOST),
                    ProtocolConstants.STANDARD_PS_PORT,
                    ProtocolConstants.STANDARD_COMMAND_PORT);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        Client.launch();
    }

    public static ServerConnector getConnector() {
        return connector;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        ChatView view = new ChatView();
        ChatViewController c = new ChatViewController();
        view.connectHandlers(c);
        primaryStage.setScene(view.scene);
        primaryStage.setHeight(600);
        primaryStage.setWidth(600);
        primaryStage.setTitle("GRNCP Chat Client");
        primaryStage.show();
    }
}
