package com.ajisaac.scrapebatch.network;

import com.ajisaac.scrapebatch.dto.JobPosting;
import com.ajisaac.scrapebatch.websocket.MessageService;
import com.ajisaac.scrapebatch.websocket.Message;

import javax.inject.Singleton;

@Singleton
public class WebsocketNotifier {

  private final MessageService ms;

  public WebsocketNotifier(MessageService ms) {
    this.ms = ms;
  }

  public void send(String message) {
    System.out.println(message);
  }

  public void error(Exception e, String siteName) {
    var msg = e.getMessage() + " when scraping " + siteName + ".";
    System.out.println(msg);
    ms.send(new Message(siteName, msg));
  }

  public void sleeping(int seconds, String siteName) {
    var msg = "Sleeping for " + seconds + " while scraping " + siteName + ".";
    System.out.println(msg);
    ms.send(new Message(siteName, msg));
  }

  public void successfulDescPageScrape(JobPosting jobPosting, String siteName) {
    var msg = "Successfully scraped " + jobPosting.getJobSite() + " - " + jobPosting.getCompany()
      + " while scraping " + siteName + ".";
    System.out.println(msg);
    ms.send(new Message(siteName, msg));
  }

  public void failedDescPageScrape(String href, String siteName) {
    var msg = "Failed to scrape " + href + " while scraping " + siteName + ".";
    System.out.println(msg);
    ms.send(new Message(siteName, msg));
  }

  public void scrapingDescPage(String href, String siteName) {
    var msg = "Scraping " + href + " from " + siteName + ".";
    System.out.println(msg);
    ms.send(new Message(siteName, msg));
  }

  public void scrapingMainPage(String href, String siteName) {
    var msg = "Scraping main page " + href + " for " + siteName + ".";
    System.out.println(msg);
    ms.send(new Message(siteName, msg));
  }

  public void failMainPageScrape(String href, String siteName) {
    var msg = "Failed scraping main page " + href + " for " + siteName + ".";
    System.out.println(msg);
    ms.send(new Message(siteName, msg));
  }

  public void successfulMainPageScrape(String href, String siteName) {
    var msg = "Success scraping main page " + href + " for " + siteName + ".";
    System.out.println(msg);
    ms.send(new Message(siteName, msg));
  }

  public void foundPostings(int size, String siteName, String href) {
    var msg = "Found " + size + " postings from " + href + " for " + siteName + ".";
    System.out.println(msg);
    ms.send(new Message(siteName, msg));
  }
}
