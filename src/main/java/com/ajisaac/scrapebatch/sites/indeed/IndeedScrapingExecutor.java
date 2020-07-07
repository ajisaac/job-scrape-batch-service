package com.ajisaac.scrapebatch.sites.indeed;

import com.ajisaac.scrapebatch.dto.JobPosting;
import com.ajisaac.scrapebatch.dto.ScrapeJob;
import com.ajisaac.scrapebatch.scrape.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.logging.log4j.util.Strings;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/** Will send requests in order to indeed to scrape it. */
public final class IndeedScrapingExecutor extends MultiPageScrapingExecutor {

  // Used for keeping track of "next page". We scrape 10 results per page generally.
  private int start = 0;
  // Details about our job search
  private ScrapeJob indeedSearch;

  public IndeedScrapingExecutor() {}

  @Override
  public JobPosting parseJobDescriptionPage(String jobDescriptionPage, JobPosting jobPosting) {

    Element parsed = Jsoup.parse(jobDescriptionPage);
    Element text = parsed.getElementById("jobDescriptionText");
    if (text != null) {
      String description = text.toString().trim();
      jobPosting.setDescription(description);
    }
    jobPosting.setStatus("new");
    return jobPosting;
  }

  @Override
  public List<JobPosting> parseMainPage(String mainPage) {

    final List<JobPosting> jobPostings = new ArrayList<>();
    // parse the page for jobs
    Document document = Jsoup.parse(mainPage);
    Element jobTable = document.getElementById("resultsCol");
    Elements jobs = jobTable.getElementsByClass("result");

    // scrape each job
    for (Element job : jobs) {
      JobPosting indeedJobPosting = jobPostingFromElement(job);
      jobPostings.add(indeedJobPosting);
    }

    // do we need to keep scraping
    boolean b = hasMoreResults(document);
    super.updateShouldKeepScraping(b);
    if (b) {
      this.start += 10;
    }

    return jobPostings;
  }

  /**
   * Given the HTML for a single basic job posting, parse it into a job posting.
   *
   * @param job the html to parse.
   * @return the scraped job.
   */
  private JobPosting jobPostingFromElement(Element job) {
    JobPosting jobPosting = new JobPosting();
    Elements jobTitles = job.getElementsByClass("jobtitle");
    if (!jobTitles.isEmpty()) {
      String jobTitle = jobTitles.get(0).text().trim();
      jobPosting.setJobTitle(jobTitle);
    }

    Elements urls = job.getElementsByClass("jobtitle");
    if (!urls.isEmpty()) {
      String path = urls.get(0).attr("href");
      String href = "https://www.indeed.com" + path;
      jobPosting.setHref(href);
    }

    Elements summaries = job.getElementsByClass("summary");
    if (!summaries.isEmpty()) {
      String summary = summaries.get(0).text().trim();
      jobPosting.setSummary(summary);
    }

    Elements companies = job.getElementsByClass("company");
    if (!companies.isEmpty()) {
      String company = companies.get(0).text().trim();
      jobPosting.setCompany(company);
    }else{
      jobPosting.setCompany("unknown");
    }

    Elements locations = job.getElementsByClass("location");
    if (!locations.isEmpty()) {
      String location = locations.get(0).text().trim();
      jobPosting.setLocation(location);
    }

    Elements dates = job.getElementsByClass("date");
    if (!dates.isEmpty()) {
      String date = dates.get(0).text().trim();
      jobPosting.setDate(date);
    }

    Elements salaries = job.select(".salary.no-wrap");
    if (!salaries.isEmpty()) {
      String salary = salaries.get(0).text().trim();
      jobPosting.setSalary(salary);
    }

    return jobPosting;
  }

  @Override
  public URI getNextMainPageURI() {
    URIBuilder uriBuilder = new URIBuilder();
    try {
      // default values
      uriBuilder.setScheme("https");
      uriBuilder.setHost("www.indeed.com");
      uriBuilder.setPath("jobs");
      uriBuilder.addParameter("q", this.indeedSearch.getQuery());
      uriBuilder.addParameter("l", this.indeedSearch.getLocation());

      if (!Strings.isBlank(indeedSearch.getSortType())) {
        uriBuilder.addParameter("sort", this.indeedSearch.getSortType());
      }

      int radius = this.indeedSearch.getRadius();
      if (radius > 0) {
        uriBuilder.addParameter("radius", String.valueOf(radius));
      }

      if (this.indeedSearch.isRemote()) {
        uriBuilder.addParameter("remotejob", "1");
      }

      if (start > 0) {
        uriBuilder.addParameter("start", String.valueOf(this.start));
      }

      return uriBuilder.build();
    } catch (URISyntaxException e) {
      return null;
    }
  }

  private static boolean hasMoreResults(Document document) {
    Element jobTable = document.getElementById("resultsCol");
    Elements jobs = jobTable.getElementsByClass("result");
    // todo what if none of these are true but it isn't a job results page
    // do we have more jobs?
    if (jobTable.children().hasClass("dupetext")) {
      // rest are duplicates
      return false;
    }
    if (document.select(".pagination > *:last-child").hasClass("np")) {
      // reached end of results
      return false;
    }
    if (jobs.isEmpty()) {
      return false;
    }
    return true;
  }

  @Override
  public JobPosting setJobSite(JobPosting jobPosting) {
    jobPosting.setJobSite(ScrapingExecutorType.INDEED.toString());
    return jobPosting;
  }

  @Override
  public void setScrapeJob(ScrapeJob scrapeJob) {
    checkNotNull(scrapeJob, "IndeedScrapingExecutor requires non null ScrapeJob.");
    this.indeedSearch = scrapeJob;
  }
}
