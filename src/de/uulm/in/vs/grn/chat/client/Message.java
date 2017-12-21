package de.uulm.in.vs.grn.chat.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by lg18 on 21.12.2017.
 */
public class Message {
    public final static Pattern linepattern = Pattern.compile("^(\\w+):(.+)$");

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
            throw new MessageFormatException(missingTags);
        } else {
            return this;
        }
    }

    public String getTag(MTag tag) {
        return tags.get(tag);
    }

    public boolean hasTag(MTag tag) {
        return tags.containsKey(tag);
    }

    public MessageType getType() {
        return mType;
    }
}
