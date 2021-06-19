package com.ajisaac.scrapebatch.network;

import com.ajisaac.scrapebatch.dto.JobPosting;
import com.ajisaac.scrapebatch.websocket.MessageService;
import com.ajisaac.scrapebatch.websocket.Message;

import javax.inject.Singleton;
import java.time.LocalTime;

@Singleton
public class WebsocketNotifier {

  private final MessageService ms;

  public WebsocketNotifier(MessageService ms) {
    this.ms = ms;
  }

  private String dateString() {
    java.time.LocalTime time = LocalTime.now();
    int hour = time.getHour();
    int minute = time.getMinute();
    int second = time.getSecond();

    String hourString, minuteString, secondString;
    hourString = hour <= 9 ? ("0" + hour) : (String.valueOf(hour));
    minuteString = minute <= 9 ? ("0" + minute) : (String.valueOf(minute));
    secondString = second <= 9 ? ("0" + second) : (String.valueOf(second));
    return hourString + ":" + minuteString + ":" + secondString + " - ";
  }

  public void send(String message, String name) {
    System.out.println(dateString() + message);
    ms.send(new Message(name, dateString() + message));
  }

  public void error(Exception e, String name) {
    var msg = dateString() + e.getMessage() + " when scraping " + name + ".";
    System.out.println(msg);
    ms.send(new Message(name, msg));
  }

  public void sleeping(int seconds, String name) {
    var msg = dateString() + "Sleeping for " + seconds + " seconds.";
    System.out.println(msg);
    ms.send(new Message(name, msg));
  }

  public void successfulDescPageScrape(JobPosting jobPosting, String name) {
    var msg = dateString() + "Successfully scraped " + jobPosting.getJobTitle() + " - " + jobPosting.getCompany() + ".";
    System.out.println(msg);
    ms.send(new Message(name, msg));
  }

  public void failedDescPageScrape(String href, String name) {
    var msg = dateString() + "Failed to scrape " + href + ".";
    System.out.println(msg);
    ms.send(new Message(name, msg));
  }

  public void scrapingDescPage(String href, String name) {
    var msg = dateString() + "Scraping " + href + ".";
    System.out.println(msg);
    ms.send(new Message(name, msg));
  }

  public void scrapingMainPage(String href, String name) {
    var msg = dateString() + "Scraping main page " + href + ".";
    System.out.println(msg);
    ms.send(new Message(name, msg));
  }

  public void failMainPageScrape(String href, String name) {
    var msg = dateString() + "Failed scraping main page " + href + ".";
    System.out.println(msg);
    ms.send(new Message(name, msg));
  }

  public void successfulMainPageScrape(String href, String name) {
    var msg = dateString() + "Success scraping main page " + href + ".";
    System.out.println(msg);
    ms.send(new Message(name, msg));
  }

  public void foundPostings(int size, String name, String href) {
    var msg = dateString() + "Found " + size + " postings from " + href + ".";
    System.out.println(msg);
    ms.send(new Message(name, msg));
  }
}
