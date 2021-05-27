package com.ajisaac.scrapebatch.scrape;

import com.ajisaac.scrapebatch.dto.Link;
import com.ajisaac.scrapebatch.dto.ScrapeJob;
import com.ajisaac.scrapebatch.scrape.executors.MultiPageScrapingExecutor;
import com.ajisaac.scrapebatch.scrape.executors.SinglePageScrapingExecutor;
import com.ajisaac.scrapebatch.scrape.scrapers.*;

public enum ScrapingExecutorType {
  INDEED("indeed.com"),
  WWR("weworkremotely.com"),
  REMOTIVEIO("remotive.io"),
  REMOTECO("remote.co"),
  REMOTEOKIO("remoteok.io"),
  SITEPOINT("sitepoint.com"),
  STACKOVERFLOW("stackoverflow.com"),
  WORKINGNOMADS("workingnomads.com");
  //  workew
  //  github
  //  ycombinator
  //  flexjobs

  private final String baseUrl;

  ScrapingExecutorType(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  /**
   * given a link, determine which site it's related to
   */
  public static ScrapingExecutorType determineSiteFromUrl(Link link) {
    if (link == null || link.getLink().isBlank()) {
      return null;
    }

    String url = link.getLink();
    for (ScrapingExecutorType s : ScrapingExecutorType.values()) {
      if (url.contains(s.baseUrl)) {
        return s;
      }
    }

    return null;
  }

  /**
   * for a generic ScrapeJob, gets the ScrapingExecutorType or null
   */
  public static ScrapingExecutorType getTypeFromScrapeJob(ScrapeJob scrapeJob) {
    try {
      return ScrapingExecutorType.valueOf(scrapeJob.getSite());
    } catch (IllegalArgumentException ex) {
      return null;
    }
  }

  /**
   * given the scrapeJob, get an executor for it
   */
  public static ScrapingExecutor getInstance(ScrapeJob scrapeJob) {
    ScrapingExecutorType type = getTypeFromScrapeJob(scrapeJob);
    if (type == null) {
      return null;
    }
    switch (type) {
      case INDEED -> {
        IndeedScraper indeedScraper = new IndeedScraper(scrapeJob);
        return new MultiPageScrapingExecutor(indeedScraper);
      }
      case WWR -> {
        Scraper wwrScraper = new WwrScraper(scrapeJob);
        return new SinglePageScrapingExecutor(wwrScraper);
      }
      case REMOTIVEIO -> {
        Scraper remoteivioScraper = new RemoteivioScraper(scrapeJob);
        return new SinglePageScrapingExecutor(remoteivioScraper);
      }
      case REMOTECO -> {
        Scraper remotecoScraper = new RemotecoScraper(scrapeJob);
        return new SinglePageScrapingExecutor(remotecoScraper);
      }
      case REMOTEOKIO -> {
        Scraper remoteokioScraper = new RemoteokioScraper(scrapeJob);
        return new SinglePageScrapingExecutor(remoteokioScraper);
      }
      case SITEPOINT -> {
        Scraper sitepointScraper = new SitepointScraper(scrapeJob);
        return new MultiPageScrapingExecutor(sitepointScraper);
      }
      case STACKOVERFLOW -> {
        Scraper stackoverflowScraper = new StackoverflowScraper(scrapeJob);
        return new MultiPageScrapingExecutor(stackoverflowScraper);
      }
      case WORKINGNOMADS -> {
        Scraper workingnomadsScraper = new WorkingNomadsScraper(scrapeJob);
        return new SinglePageScrapingExecutor(workingnomadsScraper);
      }
    }
    return null;
  }
}
