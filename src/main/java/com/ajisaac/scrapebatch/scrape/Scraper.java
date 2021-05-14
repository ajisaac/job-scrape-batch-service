package com.ajisaac.scrapebatch.scrape;

import com.ajisaac.scrapebatch.dto.JobPosting;
import com.ajisaac.scrapebatch.scrape.ScrapingExecutorType;

import java.util.List;

/** classes that implement this are capable of scraping a job board */
public interface Scraper {
  /** given an "index" page, try to parse it */
  List<JobPosting> parseMainPage(String html);

  /** given a "job description" page, try to parse it */
  void parseJobDescriptionPage(String html, JobPosting jobPosting);

  /** get the job site */
  ScrapingExecutorType getJobSite();
}
