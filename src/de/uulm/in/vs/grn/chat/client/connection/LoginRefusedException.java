package de.uulm.in.vs.grn.chat.client.connection;

public class LoginRefusedException extends Exception {
    public LoginRefusedException(String msg) {
        super(msg);
    }
}
