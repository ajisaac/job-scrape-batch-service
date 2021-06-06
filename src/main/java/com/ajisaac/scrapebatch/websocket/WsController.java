package com.ajisaac.scrapebatch.websocket;

import com.ajisaac.scrapebatch.websocket.dto.Message;
import com.ajisaac.scrapebatch.websocket.dto.OutputMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class WsController {

  @MessageMapping("/chat")
  @SendTo("/topic/messages")
  public OutputMessage send(Message message){
    String time = new SimpleDateFormat("HH:mm").format(new Date());
    return new OutputMessage(message.getFrom(), message.getText() + time);
  }
}
