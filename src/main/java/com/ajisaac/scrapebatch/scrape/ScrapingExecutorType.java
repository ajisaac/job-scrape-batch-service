package com.ajisaac.scrapebatch.scrape;

import com.ajisaac.scrapebatch.dto.ScrapeJob;
import com.ajisaac.scrapebatch.sites.indeed.IndeedScrapingExecutor;
import com.ajisaac.scrapebatch.sites.remoteco.RemotecoScrapingExecutor;
import com.ajisaac.scrapebatch.sites.remoteokio.RemoteokioScrapingExecutor;
import com.ajisaac.scrapebatch.sites.remotiveio.RemotiveioScrapingExecutor;
import com.ajisaac.scrapebatch.sites.sitepoint.SitepointScrapingExecutor;
import com.ajisaac.scrapebatch.sites.stackoverflow.StackoverflowScrapingExecutor;
import com.ajisaac.scrapebatch.sites.weworkremotely.WwrScrapingExecutor;
import com.ajisaac.scrapebatch.sites.workingnomads.WorkingNomadsScrapingExecutor;

import java.lang.reflect.InvocationTargetException;

public enum ScrapingExecutorType {
  INDEED(IndeedScrapingExecutor.class),
  WWR(WwrScrapingExecutor.class),
  REMOTIVEIO(RemotiveioScrapingExecutor.class),
  REMOTECO(RemotecoScrapingExecutor.class),
  REMOTEOKIO(RemoteokioScrapingExecutor.class),
  SITEPOINT(SitepointScrapingExecutor.class),
  STACKOVERFLOW(StackoverflowScrapingExecutor.class),
  WORKINGNOMADS(WorkingNomadsScrapingExecutor.class);
  //  workew
  //  github
  //  ycombinator
  //  flexjobs

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
   *
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
