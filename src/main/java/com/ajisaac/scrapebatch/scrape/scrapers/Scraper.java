package com.ajisaac.scrapebatch.scrape.scrapers;

import com.ajisaac.scrapebatch.dto.DatabaseService;
import com.ajisaac.scrapebatch.dto.JobPosting;
import com.ajisaac.scrapebatch.dto.ScrapeJob;
import com.ajisaac.scrapebatch.scrape.ScrapingExecutorType;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

public abstract class Scraper {

  protected final ScrapeJob scrapeJob;

  public Scraper(ScrapeJob scrapeJob) {
    this.scrapeJob = scrapeJob;
  }


  /**
   * parse a main job postings page for a list of postings
   */
  public abstract List<JobPosting> parseMainPage(String html);

  /**
   * parse the detailed description page for a single posting
   */
  public void parseJobDescriptionPage(String html, JobPosting jobPosting) {
    // the default does nothing, if we don't need to implement this
  }

  /**
   * what is the job site of this scraper
   */
  public abstract ScrapingExecutorType getJobSite();

  /**
   * what is the next page to scrape
   */
  public abstract URI getNextMainPageURI();

  /**
   * make the job description not as difficult to read
   */
  public void cleanseJobDescription(JobPosting posting) {
    // normally don't do anything
  }

  /**
   * removes the job postings that are dupes based upon href. Not every scraper can do this.
   * Some scrapers won't do anything for this.
   */
  public List<JobPosting> removeJobPostingsBasedOnHref(List<JobPosting> jobPostings, DatabaseService dbService) {
    List<String> existingHrefs = dbService.getHrefsForSite(getJobSite().name());
    return jobPostings.stream()
      .filter(jobPosting -> !existingHrefs.contains(jobPosting.getHref()))
      .collect(Collectors.toList());
  }

  public String getName() {
    if (scrapeJob == null)
      return "";
    return this.scrapeJob.getName();
  }
}
