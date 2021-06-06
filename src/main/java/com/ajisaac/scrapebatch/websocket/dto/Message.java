package com.ajisaac.scrapebatch.websocket.dto;

import java.util.Objects;

public class Message {
  private String from;
  private String text;

  public Message() {
  }

  public Message(String from, String text) {
    this.from = from;
    this.text = text;
  }

  public String getFrom() {
    return from;
  }

  public void setFrom(String from) {
    this.from = from;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  @Override
  public String toString() {
    return "Message{" +
      "from='" + from + '\'' +
      ", text='" + text + '\'' +
      '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Message message = (Message) o;
    return Objects.equals(from, message.from) && Objects.equals(text, message.text);
  }

  @Override
  public int hashCode() {
    return Objects.hash(from, text);
  }
}
