package de.uulm.in.vs.grn.chat.client.connection;

import de.uulm.in.vs.grn.chat.client.connection.events.ConnectionExpiredEvent;
import de.uulm.in.vs.grn.chat.client.connection.events.JoinEvent;
import de.uulm.in.vs.grn.chat.client.connection.events.MessageEvent;
import de.uulm.in.vs.grn.chat.client.connection.events.UserlistUpdateEvent;
import java.util.EventObject;

public interface ConnectionEventListener {

    void onConnectionEvent(EventObject event);
    void onJoinEvent(JoinEvent event);
    void onMessageEvent(MessageEvent event);
    void onUserlistUpdateEvent(UserlistUpdateEvent event);
    void onConnectionExpiredEvent(ConnectionExpiredEvent event);
}
