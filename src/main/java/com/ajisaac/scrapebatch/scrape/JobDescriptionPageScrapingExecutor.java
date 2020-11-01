package com.ajisaac.scrapebatch.scrape;

import com.ajisaac.scrapebatch.dto.DatabaseService;
import com.ajisaac.scrapebatch.network.WebsocketNotifier;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public class JobDescriptionPageScrapingExecutor implements ScrapingExecutor {

  private final String url;

  public JobDescriptionPageScrapingExecutor(String url) {
    checkNotNull(url);
    checkState(!url.isBlank());
    this.url = url;
  }


  @Override
  public void setDatabaseService(DatabaseService databaseService) {

  }

  @Override
  public void setWebsocketNotifier(WebsocketNotifier notifier) {

  }

  @Override
  public void scrape() {

  }
}
