package com.ajisaac.scrapebatch.sites.remotiveio;

import com.ajisaac.scrapebatch.dto.JobPosting;
import com.ajisaac.scrapebatch.dto.ScrapeJob;
import com.ajisaac.scrapebatch.scrape.ScrapingExecutorType;
import com.ajisaac.scrapebatch.scrape.SinglePageScrapingExecutor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class RemotiveioScrapingExecutor extends SinglePageScrapingExecutor {
  private final String remotiveioUrl = "https://remotive.io/remote-jobs/software-dev";
  private final URI uri;

  public RemotiveioScrapingExecutor() throws URISyntaxException {
    this.uri = new URI(remotiveioUrl);
  }

  @Override
  protected JobPosting parseJobDescriptionPage(String jobDescriptionPage, JobPosting jobPosting) {
    return null;
  }

  @Override
  protected List<JobPosting> parseMainPage(String pageText) {
    // parse the page for jobs
    Document document = Jsoup.parse(pageText);
    Element jobTable = document.getElementById("");
    Elements jobs = jobTable.getElementsByClass("");

    List<JobPosting> jobPostings = new ArrayList<>();
    for (Element job : jobs) {
      JobPosting jobPosting = parseBasicJobPosting(job);
      jobPostings.add(jobPosting);
    }
    return jobPostings;
  }

  private JobPosting parseBasicJobPosting(Element job){
    return null;
  }

  @Override
  protected URI getMainPageURI() {

    return null;
  }

  @Override
  public JobPosting setJobSite(JobPosting jobPosting) {
    jobPosting.setJobSite(ScrapingExecutorType.REMOTIVEIO.toString());
    return jobPosting;
  }

  @Override
  public void setScrapeJob(ScrapeJob scrapeJob) {
    // no current need, do we need this?
  }
}
