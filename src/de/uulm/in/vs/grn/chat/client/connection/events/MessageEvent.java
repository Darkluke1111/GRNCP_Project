package de.uulm.in.vs.grn.chat.client.connection.events;

import de.uulm.in.vs.grn.chat.client.connection.Message;

import java.util.EventObject;

public class MessageEvent extends EventObject {
    private Message msg;

    public MessageEvent(Object source, Message msg) {
        super(source);
        this.msg = msg;
    }

    public Message getMsg() {
        return msg;
    }
}
