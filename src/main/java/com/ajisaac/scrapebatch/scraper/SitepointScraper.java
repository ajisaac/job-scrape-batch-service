package com.ajisaac.scrapebatch.scraper;

import com.ajisaac.scrapebatch.dto.JobPosting;
import com.ajisaac.scrapebatch.dto.ScrapeJob;
import com.ajisaac.scrapebatch.scrape.ScrapingExecutorType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class SitepointScraper implements MultiPageScraper {
  private int start = 1;

  private boolean shouldKeepScraping = true;

  private final ScrapeJob scapeJob;
  public SitepointScraper(ScrapeJob scrapeJob) {
    this.scapeJob = scrapeJob;

  }

  public List<JobPosting> parseMainPage(String mainPage) {
    final List<JobPosting> jobPostings = new ArrayList<>();
    // parse the page for jobs
    Document document = Jsoup.parse(mainPage);
    Elements jobs = document.getElementsByClass("card_details");

    // scrape each job
    for (Element job : jobs) {
      JobPosting jobPosting = jobPostingFromElement(job);
      jobPostings.add(jobPosting);
    }

    // do we need to keep scraping
    this.shouldKeepScraping = hasMoreResults(document);
    if (this.shouldKeepScraping) {
      this.start += 1;
    }

    return jobPostings;
  }


  private JobPosting jobPostingFromElement(Element job) {
    JobPosting jobPosting = new JobPosting();
    Element hrefElement = job.selectFirst(".company_title>a");
    if (hrefElement == null) {
      return null;
    }
    String href = hrefElement.attr("href");
    if (href.isBlank()) {
      return null;
    }
    jobPosting.setHref("https://www.sitepoint.com" + href);

    Element titleElement = job.selectFirst(".company_title>a>h3");
    if (titleElement != null) {
      jobPosting.setJobTitle(titleElement.text());
    }

    Element companyElement = job.selectFirst(".company_details>span");
    if (companyElement != null) {
      jobPosting.setCompany(companyElement.text());
    }

    Element dateElement = job.selectFirst(".cardDate>p");
    if (dateElement != null) {
      jobPosting.setDate(dateElement.text());
    }

    Elements tagsElements = job.select(".cardTags>span");
    if (!tagsElements.isEmpty()) {
      String tags = String.join(" - ", tagsElements.eachText());
      jobPosting.setTags(tags);
    }

    return jobPosting;
  }

  public void parseJobDescriptionPage(String jobDescriptionPage, JobPosting jobPosting) {
    Document document = Jsoup.parse(jobDescriptionPage);
    Element jsonData = document.selectFirst("script[type=\"application/ld+json\"]");
    if (jsonData == null) {
      // we still have the URL at least
      return;
    }

    // all our data comes in a json object, so we will read that object.
    List<Node> nodes = jsonData.childNodes();
    if (nodes.isEmpty()) {
      return;
    }

    Node n = nodes.get(0);
    String json = n.toString().trim();

    JsonNode node;
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      node = objectMapper.readTree(json);
    } catch (JsonProcessingException e) {
      return;
    }

    JsonNode datePosted = node.get("datePosted");
    if (datePosted != null) {
      jobPosting.setDate(datePosted.asText());
    }

    JsonNode description = node.get("description");
    if (description != null) {
      jobPosting.setDescription(description.asText());
    }
  }

  @Override
  public ScrapingExecutorType getJobSite() {
    return ScrapingExecutorType.SITEPOINT;
  }

  private boolean hasMoreResults(Document document) {
    Elements e = document.select("a[rel=next]");
    return !e.isEmpty();
  }

  public void setScrapeJob(ScrapeJob scrapeJob) {
    // not needed
  }

  public URI getNextMainPageURI() {
    try {
      String url = "https://www.sitepoint.com/jobs/";
      if (start > 1) {
        url = "https://www.sitepoint.com/jobs/" + start + "/";
      }
      return new URI(url);
    } catch (URISyntaxException e) {
      return null;
    }
  }

  public JobPosting setJobSite(JobPosting jobPosting) {
    jobPosting.setJobSite(ScrapingExecutorType.SITEPOINT.toString());
    return jobPosting;
  }
}
