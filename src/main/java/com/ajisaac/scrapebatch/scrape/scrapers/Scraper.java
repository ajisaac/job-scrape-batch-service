package com.ajisaac.scrapebatch.scrape.scrapers;

import com.ajisaac.scrapebatch.dto.DatabaseService;
import com.ajisaac.scrapebatch.dto.JobPosting;
import com.ajisaac.scrapebatch.scrape.ScrapingExecutorType;

import java.net.URI;
import java.util.List;

public interface Scraper {
  /** parse a main job postings page for a list of postings */
  List<JobPosting> parseMainPage(String html);
  /** parse the detailed description page for a single posting */
  void parseJobDescriptionPage(String html, JobPosting jobPosting);
  /** what is the job site of this scraper */
  ScrapingExecutorType getJobSite();
  /** what is the next page to scrape */
  URI getNextMainPageURI();
  /** make the job description not as difficult to read */
  void cleanseJobDescription(JobPosting posting);
  /** removes the job postings that are dupes based upon href. Not every scraper can do this.
   * Some scrapers won't do anything for this.
   * @return*/
  List<JobPosting> removeJobPostingsBasedOnHref(List<JobPosting> jobPostings, DatabaseService dbService);
}
