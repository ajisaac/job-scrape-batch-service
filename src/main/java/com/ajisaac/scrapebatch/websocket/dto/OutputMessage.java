package com.ajisaac.scrapebatch.websocket.dto;

import java.util.Objects;

public class OutputMessage {
  private String from;
  private String text;

  public OutputMessage(String from, String text) {
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
    return "OutputMessage{" +
      "from='" + from + '\'' +
      ", text='" + text + '\'' +
      '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    OutputMessage that = (OutputMessage) o;
    return Objects.equals(from, that.from) && Objects.equals(text, that.text);
  }

  @Override
  public int hashCode() {
    return Objects.hash(from, text);
  }
}
