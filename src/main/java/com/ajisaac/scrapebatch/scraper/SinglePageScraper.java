package com.ajisaac.scrapebatch.scraper;

public interface SinglePageScraper extends Scraper {
  /** used by single page scrapers */
  String getMainPageHref();
}
