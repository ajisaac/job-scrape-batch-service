package com.ajisaac.scrapebatch.scraper;

import com.ajisaac.scrapebatch.dto.JobPosting;
import com.ajisaac.scrapebatch.dto.ScrapeJob;
import com.ajisaac.scrapebatch.scrape.ScrapingExecutorType;
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

public class IndeedScraper implements MultiPageScraper {

  // Used for keeping track of "next page". We scrape 10 results per page generally.
  private int start = 0;

  private boolean shouldKeepScraping = true;
  // Details about our job search
  private final ScrapeJob scrapeJob;

  public IndeedScraper(ScrapeJob scrapeJob) {
    this.scrapeJob = scrapeJob;
  }


  // modifies the job posting fleshing it out a bit
  public void parseJobDescriptionPage(String jobDescriptionPage, JobPosting jobPosting) {

    Element parsed = Jsoup.parse(jobDescriptionPage);
    Element text = parsed.getElementById("jobDescriptionText");
    if (text != null) {
      String description = text.toString().trim();
      jobPosting.setDescription(description);
    }
  }

  @Override
  public ScrapingExecutorType getJobSite() {
    return ScrapingExecutorType.INDEED;
  }

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
    this.shouldKeepScraping = hasMoreResults(document);
    if (this.shouldKeepScraping) {
      this.start += 10;
    }

    return jobPostings;
  }

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
    } else {
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

  public URI getNextMainPageURI() {
    URIBuilder uriBuilder = new URIBuilder();
    try {
      // default values
      uriBuilder.setScheme("https");
      uriBuilder.setHost("www.indeed.com");
      uriBuilder.setPath("jobs");
      uriBuilder.addParameter("q", this.scrapeJob.getQuery());
      uriBuilder.addParameter("l", this.scrapeJob.getLocation());

      if (!Strings.isBlank(scrapeJob.getSortType())) {
        uriBuilder.addParameter("sort", this.scrapeJob.getSortType());
      }

      int radius = this.scrapeJob.getRadius();
      if (radius > 0) {
        uriBuilder.addParameter("radius", String.valueOf(radius));
      }

      if (this.scrapeJob.isRemote()) {
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
}
