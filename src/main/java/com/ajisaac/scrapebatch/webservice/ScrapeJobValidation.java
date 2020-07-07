package com.ajisaac.scrapebatch.webservice;

import com.ajisaac.scrapebatch.dto.ScrapeJob;

import java.util.Optional;

public class ScrapeJobValidation {
  public Optional<Message> validate(ScrapeJob scrapeJob) {
    return Optional.empty();
  }
}
