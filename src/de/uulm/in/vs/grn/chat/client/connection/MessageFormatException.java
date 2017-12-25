package de.uulm.in.vs.grn.chat.client.connection;

import de.uulm.in.vs.grn.chat.client.ErrorPriority;

import java.util.Set;

/**
 * Created by lg18 on 21.12.2017.
 */
public class MessageFormatException extends Exception {
    ErrorPriority priority;

    public MessageFormatException(ErrorPriority priority, Set<MTag> missingTags) {
        super("The following Tags are required but are missing: " + missingTags.toString());
    }

    public MessageFormatException(ErrorPriority priority, String msg) {
        super(msg);
    }
}
