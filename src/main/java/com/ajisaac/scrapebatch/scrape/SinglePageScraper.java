package com.ajisaac.scrapebatch.scrape;

public interface SinglePageScraper extends Scraper {
  /** used by single page scrapers */
  String getMainPageHref();
}
