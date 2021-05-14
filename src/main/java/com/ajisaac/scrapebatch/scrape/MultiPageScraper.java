package com.ajisaac.scrapebatch.scrape;

import java.net.URI;

public interface MultiPageScraper extends Scraper {
  /** used by multpage scrapers */
  URI getNextMainPageURI();
}
