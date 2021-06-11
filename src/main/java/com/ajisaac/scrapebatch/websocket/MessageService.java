package com.ajisaac.scrapebatch.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

  private final SimpMessagingTemplate template;

  public MessageService(@Autowired SimpMessagingTemplate template) {
    this.template = template;
  }

  public void send(Message message) {
    template.convertAndSend("/topic/messages", message);
  }
}
