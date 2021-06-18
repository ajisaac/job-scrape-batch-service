package com.ajisaac.scrapebatch.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

@ApplicationScoped
@ServerEndpoint("/topic/messages")
public class MessageService {


  private Session session;
  private final ObjectMapper mapper = new ObjectMapper();

  @OnOpen
  public void onOpen(Session session) {
    this.session = session;
  }

  @OnClose
  public void onClose() {
    this.session = null;
  }

  @OnError
  public void onError(Throwable throwable) {
    broadcast("{error: " + throwable + "}");
  }

  @OnMessage
  public void onMessage(String message) {
    System.out.println(message);
  }

  private void broadcast(String message) {
    if (this.session != null) {
      session.getAsyncRemote().sendObject(message);
    }
  }

  public void send(Message message) {
    try {
      String msg = mapper.writeValueAsString(message);
      broadcast(msg);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }
}
