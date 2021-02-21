package com.ajisaac.scrapebatch.frontend.scraper;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ScrapeWebsocketController {

  @MessageMapping("/notifications")
  @SendTo("/topic/notifications")
  public String helloWorld(String message){
    return "hello";
  }

}
