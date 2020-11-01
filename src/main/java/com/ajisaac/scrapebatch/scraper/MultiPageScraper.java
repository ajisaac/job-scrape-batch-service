package com.ajisaac.scrapebatch.scraper;

import java.net.URI;

public interface MultiPageScraper extends Scraper {
  /** used by multpage scrapers */
  URI getNextMainPageURI();
}
