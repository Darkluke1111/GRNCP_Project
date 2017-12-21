package de.uulm.in.vs.grn.chat.client.view;

import javafx.scene.input.KeyEvent;

/**
 * Created by lg18 on 21.12.2017.
 */
public class ChatViewController {
    public ChatView view;

    public void handleSendMessage(String text) {
        view.inputBox.setText("");
        System.out.println(text);
    }
}
