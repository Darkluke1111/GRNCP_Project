package de.uulm.in.vs.grn.chat.client;

import java.util.regex.Pattern;

/**
 * Created by lg18 on 21.12.2017.
 */
public class ProtocolConstants {
    public static final String VERSION = "GRNCP/0.1";
    public static final Pattern TAG_LAYOUT = Pattern.compile("^(\\w+):(.+)$");
}
