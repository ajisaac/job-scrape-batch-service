package com.ajisaac.scrapebatch.network;

import com.ajisaac.scrapebatch.dto.JobPosting;
import org.springframework.stereotype.Service;

/** sending messages here will make their way to the frontend */
@Service
public class WebsocketNotifier {
  /** sends a generic message on the websocket to the frontend */
  public void send(String message) {
    System.out.println(message);
  }

  /** sends generic exception messages to the frontend */
  public void error(Exception e, String siteName) {
    System.out.println(e.getMessage() + " when scraping " + siteName + ".");
  }

  /** notifies that we are sleeping */
  public void sleeping(int seconds, String siteName) {
    System.out.println("Sleeping for " + seconds + " while scraping " + siteName + ".");
  }

  /** when we have successfully scraped a description page */
  public void successfulDescPageScrape(JobPosting jobPosting, String siteName) {
    System.out.println(
        "Successfully scraped "
            + jobPosting.getJobSite()
            + " - "
            + jobPosting.getCompany()
            + " while scraping "
            + siteName
            + ".");
  }

  /** notify when description page scraping failed */
  public void failedDescPageScrape(String href, String siteName) {
    System.out.println("Failed to scrape " + href + " while scraping " + siteName + ".");
  }

  /** notify that we are scraping a description page */
  public void scrapingDescPage(String href, String siteName) {
    System.out.println("Scraping " + href + " from " + siteName + ".");
  }

  /** notify that we are scraping the main page */
  public void scrapingMainPage(String href, String siteName) {
    System.out.println("Scraping main page " + href + " for " + siteName + ".");
  }

  /** notify if main page scrape was a failure */
  public void failMainPageScrape(String href, String siteName) {
    System.out.println("Failed scraping main page " + href + " for " + siteName + ".");
  }

  /** notify if main page scrape was a success */
  public void successfulMainPageScrape(String href, String siteName) {
    System.out.println("Success scraping main page " + href + " for " + siteName + ".");
  }

  /** notify how many job postings were found */
  public void foundPostings(int size, String siteName, String href) {
    System.out.println("Found " + size + " postings from " + href + " for " + siteName + ".");
  }
}
