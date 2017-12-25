package de.uulm.in.vs.grn.chat.client.connection;

import java.util.*;

/**
 * Created by lg18 on 21.12.2017.
 */
public enum MessageType {
    LOGIN(false,MTag.Username),
    LOGGEDIN(true,MTag.Date),
    SEND(false,MTag.Text),
    PING(false),
    BYE(false),
    SENT(true,MTag.Date),
    PONG(true,MTag.Date,MTag.Usernames),
    EXPIRED(true,MTag.Date),
    ERROR(true,MTag.Date,MTag.Reason),
    BYEBYE(true,MTag.Date),
    MESSAGE(true,MTag.Date,MTag.Username,MTag.Text),
    EVENT(true,MTag.Date,MTag.Description);

    private Set<MTag> requiredTags;
    boolean fromServer;

    MessageType( boolean fromServer,MTag ... requiredTags) {
        this.requiredTags = new HashSet<>(Arrays.asList(requiredTags));
    }

    public Set<MTag> getRequiredTags() {
        return requiredTags;
    }
}
