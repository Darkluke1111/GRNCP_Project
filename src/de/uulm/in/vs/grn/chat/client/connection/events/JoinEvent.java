package de.uulm.in.vs.grn.chat.client.connection.events;

import de.uulm.in.vs.grn.chat.client.connection.Message;

public class JoinEvent extends ConnectionEvent {
  Message msg;
  public JoinEvent(Object source, Message joinMsg) {
    super(source);
    this.msg = joinMsg;
  }

  public Message getMsg() {
    return msg;
  }
}
