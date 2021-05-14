package com.ajisaac.scrapebatch.scrape;

import com.ajisaac.scrapebatch.dto.DatabaseService;
import com.ajisaac.scrapebatch.network.WebsocketNotifier;

/** This will take a scraper and execute it */
public interface ScrapingExecutor {
  /** whatever we scrape will probably need to be put into the database */
  void setDatabaseService(DatabaseService databaseService);

  /** sets the messaging system for this scraper */
  void setWebsocketNotifier(WebsocketNotifier notifier);

  /** scrape the scrape job site */
  void scrape();

  /** * stops scraping */
  void stopScraping();
}
