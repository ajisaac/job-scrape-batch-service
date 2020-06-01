package com.ajisaac.scrapebatch;

/** A error class that serves the purpose of being output as Json. */
public class OutputError {
  private final String error;
  private final String description;

  /** A error class that serves the purpose of being output as Json. */
  public OutputError(String error, String description) {
    this.error = error;
    this.description = description;
  }

  public String getDescription() {
    return description;
  }

  public String getError() {
    return error;
  }
}
