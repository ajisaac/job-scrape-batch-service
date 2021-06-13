package com.ajisaac.scrapebatch.scrape.scrapers;

import com.ajisaac.scrapebatch.dto.DatabaseService;
import com.ajisaac.scrapebatch.dto.JobPosting;
import com.ajisaac.scrapebatch.dto.ScrapeJob;
import com.ajisaac.scrapebatch.scrape.ScrapingExecutorType;
import org.apache.http.client.utils.URIBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class WwrScraper implements Scraper {

//  private final ScrapeJob scrapeJob;

  public WwrScraper(ScrapeJob scrapeJob) {
//    this.scrapeJob = scrapeJob;
  }

  public List<JobPosting> parseMainPage(String pageText) {
    // parse the page for jobs
    Document document = Jsoup.parse(pageText);
    Element jobTable = document.getElementById("job_list");
    if (jobTable == null) return new ArrayList<>();
    Elements jobsList = jobTable.getElementsByTag("ul");
    if (jobsList.isEmpty()) return new ArrayList<>();
    Elements jobsLiItems = jobsList.get(0).getElementsByTag("li");
    if (jobsLiItems.isEmpty()) return new ArrayList<>();

    List<JobPosting> jobPostings = new ArrayList<>();
    for (Element job : jobsLiItems) {
      if (job.id().equals("one-signal-subscription-form")) {
        continue;
      }

      JobPosting jobPosting = parseBasicJobPosting(job);
      if (jobPosting != null) {
        jobPostings.add(jobPosting);
      }
    }
    return jobPostings;
  }

  private JobPosting parseBasicJobPosting(Element job) {
    JobPosting jobPosting = new JobPosting();
    jobPosting.setJobSite(ScrapingExecutorType.WWR.toString());

    // get the link to the job posting
    Elements anchors = job.getElementsByTag("a");
    for (Element anchor : anchors) {
      Attributes anchorAttrs = anchor.attributes();
      for (Attribute att : anchorAttrs) {
        String key = att.getKey().toLowerCase();
        if (key.equals("href")) {
          String value = att.getValue();
          if (value.trim().startsWith("/remote-jobs") || value.trim().startsWith("/listings")) {
            jobPosting.setHref("https://weworkremotely.com" + value);
          }
        }
      }
    }
    if (jobPosting.getHref() == null || jobPosting.getHref().isBlank()) {
      // no point to continue without a url
      return null;
    }

    // get company
    Elements companies = job.getElementsByClass("company");
    if (!companies.isEmpty()) {
      String company = companies.get(0).text();
      jobPosting.setCompany(company);
    }

    // get region
    Elements regions = job.getElementsByClass("region");
    if (!regions.isEmpty()) {
      String region = regions.get(0).text();
      jobPosting.setRemoteText(region);
    }

    // get title
    Elements titles = job.getElementsByClass("title");
    if (!titles.isEmpty()) {
      String title = titles.get(0).text();
      jobPosting.setJobTitle(title);
    }

    return jobPosting;
  }

  public void parseJobDescriptionPage(String jobDescriptionPage, JobPosting jobPosting) {
    // validate
    if (jobDescriptionPage == null || jobDescriptionPage.isBlank()) {
      return;
    }

    // get main content
    Document document = Jsoup.parse(jobDescriptionPage);
    Elements contents = document.getElementsByClass("content");
    Element content = contents.first();
    if (content == null) {
      return;
    }

    // get date posted
    Elements times = content.getElementsByTag("time");
    Element time = times.first();
    if (time != null) {
      String dateTime = time.attr("datetime");
      if (!dateTime.isBlank()) {
        jobPosting.setDate(dateTime);
      }
    }

    // get misc text
    Elements miscs = content.select("span.listing-tag");
    StringBuilder miscText = new StringBuilder();
    int length = miscs.size();
    for (int i = 0; i < length; i++) {
      Element misc = miscs.get(i);
      String text = misc.text();
      if (!text.isBlank()) {
        miscText.append(text);
        if (i != length - 1) {
          miscText.append(" - ");
        }
      }
    }
    jobPosting.setMiscText(miscText.toString());

    // get description
    Element description = content.getElementById("job-listing-show-container");
    if (description != null) {
      String text = description.toString().trim();
      jobPosting.setDescription(text);
    }
  }

  @Override
  public ScrapingExecutorType getJobSite() {
    return ScrapingExecutorType.WWR;
  }


  //private final String WwrHref = "https://weworkremotely.com/categories/remote-back-end-programming-jobs";
//private final String WwrHref = "https://weworkremotely.com/categories/remote-full-stack-programming-jobs";
//private final String WwrHref = "https://weworkremotely.com/categories/remote-front-end-programming-jobs";
  public URI getNextMainPageURI() {
    var uriBuilder = new URIBuilder();
    try {
      // default values
      uriBuilder.setScheme("https");
      uriBuilder.setHost("weworkremotely.com");
//      uriBuilder.setPath("categories/remote-back-end-programming-jobs");
       uriBuilder.setPath("categories/remote-full-stack-programming-jobs");
      // uriBuilder.setPath("categories/remote-front-end-programming-jobs");
      return uriBuilder.build();
    } catch (URISyntaxException e) {
      return null;
    }
  }

  @Override
  public void cleanseJobDescription(JobPosting posting) {
    String desc = posting.getDescription();
    /*
    for each pair of line breaks turn it into a single line break

     */
  }

  @Override
  public List<JobPosting> removeJobPostingsBasedOnHref(List<JobPosting> jobPostings, DatabaseService dbService) {
    // remove job postings that already exist
    List<String> existingHrefs = dbService.getHrefsForSite("WWR");
    return jobPostings.stream()
      .filter(jobPosting -> !existingHrefs.contains(jobPosting.getHref()))
      .collect(Collectors.toList());
  }
}
