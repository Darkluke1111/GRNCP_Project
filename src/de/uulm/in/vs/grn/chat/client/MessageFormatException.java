package de.uulm.in.vs.grn.chat.client;

import java.util.Set;

/**
 * Created by lg18 on 21.12.2017.
 */
public class MessageFormatException extends Exception {

    public MessageFormatException(Set<MTag> missingTags) {
        super("The following Tags are required but are missing: " + missingTags.toString());
    }

    public MessageFormatException(String msg) {
        super(msg);
    }
}
