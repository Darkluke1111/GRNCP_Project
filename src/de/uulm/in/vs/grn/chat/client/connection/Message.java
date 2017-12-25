package de.uulm.in.vs.grn.chat.client.connection;

import de.uulm.in.vs.grn.chat.client.ErrorPriority;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by lg18 on 21.12.2017.
 */
public class Message {
    protected final  Map<MTag,String> tags;
    private final MessageType mType;

    private Message(MessageType mType) {
        this.mType = mType;
        tags = new HashMap<>();
    }

    public static Message newMessage(MessageType mType) {
        return new Message(mType);
    }

    public Message withTag(MTag tag, String content) {
        tags.put(tag, content);
        return this;
    }

    public Message withTags(Map<MTag, String> tags) {
        this.tags.putAll(tags);
        return this;
    }

    public Message validate() throws MessageFormatException {
        Set<MTag> missingTags = mType
                .getRequiredTags()
                .stream()
                .filter(reqTag -> !tags.containsKey(reqTag))
                .collect(Collectors.toSet());

        if(!missingTags.isEmpty()) {
            throw new MessageFormatException(ErrorPriority.ERROR, missingTags);
        } else {
            return this;
        }
    }

    public static Message buildSendMessage(String text) throws MessageFormatException {
        Message msg = new Message(MessageType.SEND)
            .withTag(MTag.Text, text)
            .validate();
        return msg;
    }

    public static Message buildLoginMessage(String usrName) throws MessageFormatException {
        Message msg = new Message(MessageType.LOGIN)
            .withTag(MTag.Username, usrName)
            .validate();
        return msg;
    }

    public static Message buildPingMessage() throws MessageFormatException {
        Message msg = new Message(MessageType.PING)
            .validate();
        return msg;
    }

    public static Message buildByeMessage() throws MessageFormatException {
        Message msg = new Message(MessageType.BYE)
            .validate();
        return msg;
    }

    public Map<MTag,String> getTags() {
        return tags;
    }

    public String getTagContent(MTag tag) {
        return tags.get(tag);
    }

    public boolean hasTag(MTag tag) {
        return tags.containsKey(tag);
    }

    public MessageType getType() {
        return mType;
    }

}
