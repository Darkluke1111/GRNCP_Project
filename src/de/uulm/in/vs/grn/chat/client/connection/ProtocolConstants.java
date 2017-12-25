package de.uulm.in.vs.grn.chat.client.connection;

import java.util.regex.Pattern;

/**
 * Created by lg18 on 21.12.2017.
 */
public class ProtocolConstants {
    public static final String VERSION = "GRNCP/0.1";
    public static final Pattern TAG_LAYOUT = Pattern.compile("^(\\w+):(.+)$");
    public static final String STANDARD_HOST = "134.60.77.151";
    public static final int STANDARD_PS_PORT = 8123;
    public static final int STANDARD_COMMAND_PORT = 8122;
}
