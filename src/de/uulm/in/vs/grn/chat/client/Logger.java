package de.uulm.in.vs.grn.chat.client;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * Created by lg18 on 21.12.2017.
 */
public class Logger {
    private static OutputStream logStream;
    private static PrintWriter writer;

    public static Logger logger = new Logger();

    private final static String infoPrefix = "|INFO|: ";
    private final static String waringPrefix = "|WARNING|: ";
    private final static String errorPrefix = "|ERROR|: ";

    public Logger() {
        this.logStream = System.out;
        this.writer = new PrintWriter(new OutputStreamWriter(logStream), true);
    }

    public static void logInfo(String info) {
        writer.println(infoPrefix + info);
    }

    public static void logWarning(String warning) {
        writer.println(waringPrefix + warning);
    }

    public static void logError(String error) {
        writer.println(errorPrefix + error);
    }

    public static OutputStream getLogStream() {
        return logStream;
    }
}
