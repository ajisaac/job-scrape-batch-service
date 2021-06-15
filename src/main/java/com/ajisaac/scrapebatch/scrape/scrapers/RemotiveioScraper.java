package com.ajisaac.scrapebatch.scrape.scrapers;

import com.ajisaac.scrapebatch.dto.DatabaseService;
import com.ajisaac.scrapebatch.dto.JobPosting;
import com.ajisaac.scrapebatch.dto.ScrapeJob;
import com.ajisaac.scrapebatch.scrape.ScrapingExecutorType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.utils.URIBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class RemotiveioScraper implements Scraper {
  private final String remotiveioUrl = "https://remotive.io/remote-jobs/software-dev";

  private final ScrapeJob scrapeJob;

  public RemotiveioScraper(ScrapeJob scrapeJob) {
    this.scrapeJob = scrapeJob;
  }

  public List<JobPosting> parseMainPage(String mainPage) {

    // parse the page for jobs
    Document document = Jsoup.parse(mainPage);
    Elements jobs = document.getElementsByClass("job-list-item");
    List<JobPosting> jobPostings = new ArrayList<>();

    for (Element job : jobs) {
      JobPosting jobPosting = parseBasicJobPosting(job);
      if (jobPosting != null) {
        jobPostings.add(jobPosting);
      }
    }
    return jobPostings;
  }

  /**
   * Parse the basic job that we got from the main page.
   */
  private JobPosting parseBasicJobPosting(Element job) {

    JobPosting jobPosting = new JobPosting();

    String href = parseHref(job);
    if (href == null) {
      return null;
    }
    jobPosting.setHref(href);
    String jobTitle = parseJobTitle(job);
    jobPosting.setJobTitle(jobTitle);
    String tags = parseJobTags(job);
    jobPosting.setTags(tags);
    String company = parseCompany(job);
    jobPosting.setCompany(company);
    String location = parseLocation(job);
    jobPosting.setLocation(location);

    return jobPosting;
  }

  public void parseJobDescriptionPage(String jobDescriptionPage, JobPosting jobPosting) {
    Document document = Jsoup.parse(jobDescriptionPage);
    Element jsonData = document.selectFirst("html>head>script[type=\"application/ld+json\"]");
    if (jsonData == null) {
      // we still have the URL at least
      return;
    }

    // all our data comes in a json object, so we will read that object.
    List<Node> nodes = jsonData.childNodes();
    if (nodes.isEmpty()) {
      return;
    }

    String json = nodes.get(0).toString().trim();
    JsonNode node;
    try {
      node = new ObjectMapper().readTree(json);
    } catch (JsonProcessingException e) {
      return;
    }

    JsonNode description = node.get("description");
    if (description != null) {
      jobPosting.setDescription(description.asText());
    }

    JsonNode jobLocationType = node.get("jobLocationType");
    if (jobLocationType != null) {
      jobPosting.setMiscText(jobLocationType.asText());
    }

    JsonNode baseSalary = node.get("baseSalary");
    if (baseSalary != null) {
      JsonNode baseSalaryValue = baseSalary.get("value");
      JsonNode baseSalaryCurrency = baseSalary.get("currency");
      String salary = "";
      if (baseSalaryValue != null) {
        salary = salary + baseSalaryValue.asText();
      }
      if (baseSalaryCurrency != null) {
        salary = salary + " " + baseSalaryCurrency.asText();
      }
      jobPosting.setSalary(salary);
    }

    JsonNode datePosted = node.get("datePosted");
    if (datePosted != null) {
      jobPosting.setDate(datePosted.asText());
    }

    JsonNode employmentType = node.get("employmentType");
    if (employmentType != null) {
      String misc = jobPosting.getMiscText();
      jobPosting.setMiscText(misc + " - " + employmentType.asText());
    }

    JsonNode applicationLocationRequirements = node.get("applicationLocationRequirements");
    if (applicationLocationRequirements != null) {
      JsonNode name = node.get("name");
      if (name != null) {
        String misc = jobPosting.getMiscText();
        jobPosting.setMiscText(misc + " - " + name.asText());
      }
    }
  }

  @Override
  public ScrapingExecutorType getJobSite() {
    return ScrapingExecutorType.REMOTIVEIO;
  }

  /**
   * Tries to get the url, or returns null.
   */
  private String parseHref(Element job) {
    String rel = job.attr("data-url");
    if (rel.isBlank()) {
      return null;
    } else {
      return "https://remotive.io" + rel;
    }
  }

  /**
   * Tries to get the job title or returns "".
   */
  private String parseJobTitle(Element job) {
    Elements positions = job.getElementsByClass("position");
    if (positions.isEmpty()) {
      return "";
    }
    Element position = positions.first();
    Elements positionLinks = position.getElementsByTag("a");
    if (positionLinks.isEmpty()) {
      return "";
    }
    Element positionLink = positionLinks.first();
    return positionLink.text();
  }

  /**
   * Tries to get the job tags or returns "".
   */
  private String parseJobTags(Element job) {
    Elements jobTags = job.getElementsByClass("job-tag");
    if (jobTags.isEmpty()) {
      return "";
    }
    StringBuilder ret = new StringBuilder();
    for (Element jobTag : jobTags) {
      if (!jobTag.text().isBlank()) {
        ret.append(jobTag.text());
        ret.append(" ");
      }
    }
    return ret.toString().trim();
  }

  /**
   * Tries to get the company name or returns "".
   */
  private String parseCompany(Element job) {
    Elements companies = job.getElementsByClass("company");
    if (companies.isEmpty()) {
      return "";
    }
    Element company = companies.first();
    Elements companySpans = company.getElementsByTag("span");
    if (companySpans.isEmpty()) {
      return "";
    }
    if (companySpans.size() == 1) {
      // this could be location or just the company name
      Element firstSpan = companySpans.first();
      if (firstSpan.hasClass("location")) {
        return "";
      } else {
        return firstSpan.text();
      }
    } else {
      return companySpans.first().text();
    }
  }

  /**
   * Tries to get the company location or returns "".
   */
  private String parseLocation(Element job) {
    Elements companies = job.getElementsByClass("company");
    if (companies.isEmpty()) {
      return "";
    }
    Element company = companies.first();
    Elements companySpans = company.getElementsByTag("span");
    if (companySpans.isEmpty()) {
      return "";
    }
    if (companySpans.size() == 1) {
      // this could be location or just the company name
      Element firstSpan = companySpans.first();
      if (firstSpan.hasClass("location")) {
        return firstSpan.text();
      } else {
        return "";
      }
    } else {
      return companySpans.get(1).text();
    }
  }

  public JobPosting setJobSite(JobPosting jobPosting) {
    jobPosting.setJobSite(ScrapingExecutorType.REMOTIVEIO.toString());
    return jobPosting;
  }

  public void setScrapeJob(ScrapeJob scrapeJob) {
    // no current need, do we need this?
  }

  public URI getNextMainPageURI() {
    var uriBuilder = new URIBuilder();
    try {
      // default values
      uriBuilder.setScheme("https");
      uriBuilder.setHost("remotive.io");
      uriBuilder.setPath("remote-jobs/software-dev");
      return uriBuilder.build();
    } catch (URISyntaxException e) {
      return null;
    }
  }

  @Override
  public void cleanseJobDescription(JobPosting posting) {

  }

  @Override
  public List<JobPosting> removeJobPostingsBasedOnHref(List<JobPosting> jobPostings, DatabaseService dbService) {
    return jobPostings;
  }
}
