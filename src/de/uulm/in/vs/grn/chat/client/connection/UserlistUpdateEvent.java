package de.uulm.in.vs.grn.chat.client.connection;

import java.util.EventObject;

public class UserlistUpdateEvent extends EventObject {
    Message msg;
    public UserlistUpdateEvent(Object source, Message msg) {
        super(source);
        this.msg = msg;
    }
}
