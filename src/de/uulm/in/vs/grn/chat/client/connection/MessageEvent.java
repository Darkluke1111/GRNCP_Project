package de.uulm.in.vs.grn.chat.client.connection;

import java.util.EventObject;

public class MessageEvent extends EventObject {
    Message msg;

    public MessageEvent(Object source, Message msg) {
        super(source);
        this.msg = msg;
    }
}
