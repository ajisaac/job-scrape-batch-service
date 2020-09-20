package com.ajisaac.scrapebatch.scrape;

import com.ajisaac.scrapebatch.dto.DatabaseService;
import com.ajisaac.scrapebatch.dto.ScrapeJob;
import com.ajisaac.scrapebatch.dto.JobPosting;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * Allows us to scrape a site, create an implementation that knows how to scrape a particular type
 * of job site. A differentiation might be that the job site works with or without javascript, or it
 * shows all the listings on the same page, or maybe it requires pagination to scrape all the
 * listings.
 */
public abstract class ScrapingExecutor {

  protected int pauseTime = 10;

  private DatabaseService databaseService;

  /**
   * We need a service to be able to persist our scraped jobs.
   *
   * @param databaseService Something capable of inserting jobs.
   */
  public void setDatabaseService(DatabaseService databaseService) {
    this.databaseService = databaseService;
  }

  public JobPosting storeInDatabase(JobPosting jobPosting) {
    jobPosting = databaseService.storeJobPostingInDatabase(jobPosting);
    return jobPosting;
  }

  /**
   * Sets the job site and returns the modified JobPosting.
   *
   * @param jobPosting The job posting to set the site on.
   * @return The modified job posting.
   */
  public abstract JobPosting setJobSite(JobPosting jobPosting);

  /** Scrapes the site. */
  public abstract void scrape();

  /**
   * Set the scrape job if needed.
   *
   * @param scrapeJob ScrapeJob needed for your particular executor
   */
  public abstract void setScrapeJob(ScrapeJob scrapeJob);

  /**
   * Given the HTML of a single job description page, this should be able to parse that and return a
   * JobPostingDetails.
   *
   * @param jobDescriptionPage The HTML of the jobDescriptionPage.
   * @param jobPosting The jobPosting to fill out.
   * @return The updated jobPosting.
   */
  protected abstract JobPosting parseJobDescriptionPage(
      String jobDescriptionPage, JobPosting jobPosting);

  /**
   * Given the HTML of a main page of a site, this will parse that main page into a list of Job
   * Postings.
   *
   * @param mainPage The HTML of the main page for the site to parse.
   * @return A List of JobPostings.
   */
  protected abstract List<JobPosting> parseMainPage(String mainPage);

  /**
   * Grabs a single page from a URL
   *
   * @param uri The uri in URI format to grab.
   * @return The page as HTML or null if something went wrong.
   */
  protected String grabPage(URI uri) {
    if (uri == null) {
      return null;
    }
    String ret = new PageRequest(uri).sendGet();
    if(ret == null || ret.isBlank()){
      return null;
    }
    return ret;
  }

  /**
   * Grabs a single page from a String url.
   *
   * @param href The String url to parse.
   * @return The page.
   * @throws URISyntaxException The url wasn't valid.
   */
  protected String grabPage(String href) throws URISyntaxException {
    if(href == null || href.isBlank()){
      return null;
    }
    URI uri = new URI(href);
    return grabPage(uri);
  }

  /**
   * Allows us to adjust the max amount of time we might pause between network requests.
   *
   * @param seconds the max possible time we would pause between requests in seconds.
   */
  protected final void updateMaxPauseTime(int seconds) {
    this.pauseTime = seconds;
  }

  /**
   * This should explain to the scraper how to stagger requests to simulate being a human.
   *
   * @param maxSeconds The max amount of seconds to pause, the min will be 1 second.
   */
  protected void pause(int maxSeconds) {
    try {
      int r = ThreadLocalRandom.current().nextInt(maxSeconds) + 1;
      System.out.println("Sleeping for " + r + " seconds.");
      TimeUnit.SECONDS.sleep(r);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
