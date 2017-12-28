package de.uulm.in.vs.grn.chat.client.connection;

import java.util.EventObject;

public interface EventHandler {

  void registerListener(ConnectionEventListener listener);
  void spreadEvent(EventObject event);
}
