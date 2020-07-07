package com.ajisaac.scrapebatch.webservice;

public enum GenericMessage implements Message {
  EXECUTOR_NOT_AVAILABLE("Executor not available");

  private final String errorMessage;

  GenericMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  @Override
  public String getMessage() {
    return this.errorMessage;
  }
}
