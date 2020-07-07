package com.ajisaac.scrapebatch.webservice;

public enum ScrapeJobMessage implements Message {
  JOB_NOT_FOUND("ScrapeJob not found."),
  JOB_SITE_NOT_VALID("Job site not valid."),
  ALREADY_EXECUTING("Already executing this job.");

  private final String errorMessage;

  ScrapeJobMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  @Override
  public String getMessage() {
    return this.errorMessage;
  }
}
