package com.ajisaac.scrapebatch.network;

import com.ajisaac.scrapebatch.dto.JobPosting;
import org.springframework.stereotype.Service;

@Service
public class WebsocketNotifier {
  public void send(String message) {
    System.out.println(message);
  }

  public void error(Exception e, String siteName) {
    System.out.println(e.getMessage() + " when scraping " + siteName + ".");
  }

  public void sleeping(int seconds, String siteName) {
    System.out.println("Sleeping for " + seconds + " while scraping " + siteName + ".");
  }

  public void successfulDescPageScrape(JobPosting jobPosting, String siteName) {
    System.out.println(
      "Successfully scraped " + jobPosting.getJobSite() + " - " + jobPosting.getCompany()
        + " while scraping " + siteName + ".");
  }

  public void failedDescPageScrape(String href, String siteName) {
    System.out.println("Failed to scrape " + href + " while scraping " + siteName + ".");
  }

  public void scrapingDescPage(String href, String siteName) {
    System.out.println("Scraping " + href + " from " + siteName + ".");
  }

  public void scrapingMainPage(String href, String siteName) {
    System.out.println("Scraping main page " + href + " for " + siteName + ".");
  }

  public void failMainPageScrape(String href, String siteName) {
    System.out.println("Failed scraping main page " + href + " for " + siteName + ".");
  }

  public void successfulMainPageScrape(String href, String siteName) {
    System.out.println("Success scraping main page " + href + " for " + siteName + ".");
  }

  public void foundPostings(int size, String siteName, String href) {
    System.out.println("Found " + size + " postings from " + href + " for " + siteName + ".");
  }
}
