package de.uulm.in.vs.grn.chat.client;

/**
 * Created by lg18 on 21.12.2017.
 */
public enum MTag {
    Date,
    Username,
    Text,
    Usernames,
    Description,
    Reason;

    public static String formatTag(MTag tag, String content) {
        if(tag == MTag.Text) {
            //TODO handle long messages
        }
        if(tag == MTag.Username) {
            //TODO handle wrong usernames
        }
        return tag.toString() + ": " + content + "\r\n";
    }
}
