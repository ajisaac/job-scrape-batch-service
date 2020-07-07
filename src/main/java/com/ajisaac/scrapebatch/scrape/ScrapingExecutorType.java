package com.ajisaac.scrapebatch.scrape;

import com.ajisaac.scrapebatch.dto.ScrapeJob;
import com.ajisaac.scrapebatch.sites.indeed.IndeedScrapingExecutor;
import com.ajisaac.scrapebatch.sites.remotiveio.RemotiveioScrapingExecutor;
import com.ajisaac.scrapebatch.sites.weworkremotely.WwrScrapingExecutor;

import java.lang.reflect.InvocationTargetException;

public enum ScrapingExecutorType {
  INDEED(IndeedScrapingExecutor.class),
  WWR(WwrScrapingExecutor.class),
  REMOTIVEIO(RemotiveioScrapingExecutor.class);
//  remoteco
//  remoteok
//  workew
//  sitepoint
//  stackoverflow
//  github
//  ycombinator
//  flexjobs
//  remotelyawesomejobs

  Class<? extends ScrapingExecutor> clazz;

  ScrapingExecutorType(Class<? extends ScrapingExecutor> clazz) {
    this.clazz = clazz;
  }

  public Class<? extends ScrapingExecutor> getClazz() {
    return this.clazz;
  }

  /**
   * For a generic ScrapeJob, gets the ScrapingExecutorType or null;
   *
   * @param scrapeJob
   * @return
   */
  public static ScrapingExecutorType GetTypeFromScrapeJob(ScrapeJob scrapeJob) {
    try {
      return ScrapingExecutorType.valueOf(scrapeJob.getSite());
    } catch (IllegalArgumentException ex) {
      return null;
    }
  }

  /**
   * Creates a new instance of the ScrapingExecutor of a particular type.
   * @return
   */
  public ScrapingExecutor getInstance() {
    try {
      return this.clazz.getDeclaredConstructor().newInstance();
    } catch (InstantiationException
        | NoSuchMethodException
        | IllegalAccessException
        | InvocationTargetException e) {
      return null;
    }
  }
}
