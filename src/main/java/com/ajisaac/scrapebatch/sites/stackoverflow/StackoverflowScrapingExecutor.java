package com.ajisaac.scrapebatch.sites.stackoverflow;

import com.ajisaac.scrapebatch.dto.JobPosting;
import com.ajisaac.scrapebatch.dto.ScrapeJob;
import com.ajisaac.scrapebatch.scrape.MultiPageScrapingExecutor;

import java.net.URI;
import java.util.List;

public class StackoverflowScrapingExecutor extends MultiPageScrapingExecutor {

  @Override
  protected URI getNextMainPageURI() {
    return null;
  }

  @Override
  public JobPosting setJobSite(JobPosting jobPosting) {

    return null;
  }

  @Override
  protected JobPosting parseJobDescriptionPage(String jobDescriptionPage, JobPosting jobPosting) {
    return null;
  }

  @Override
  protected List<JobPosting> parseMainPage(String mainPage) {
    return null;
  }

  @Override
  public void setScrapeJob(ScrapeJob scrapeJob) {
    // not needed
  }

}
