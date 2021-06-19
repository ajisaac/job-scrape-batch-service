package com.ajisaac.scrapebatch.scrape.scrapers;

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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class StackoverflowScraper extends Scraper {

  private int start = 1;


  public StackoverflowScraper(ScrapeJob scrapeJob) {
    super(scrapeJob);
  }

  public List<JobPosting> parseMainPage(String mainPage) {
    final List<JobPosting> jobPostings = new ArrayList<>();
    // parse the page for jobs
    Document document = Jsoup.parse(mainPage);
    Element jsonData = document.selectFirst("script[type=\"application/ld+json\"]");
    if (jsonData == null) {
      // we still have the URL at least
      return jobPostings;
    }

    // all our data comes in a json object, so we will read that object.
    List<Node> nodes = jsonData.childNodes();
    if (nodes.isEmpty()) {
      return jobPostings;
    }

    Node n = nodes.get(0);
    String json = n.toString().trim();

    JsonNode node;
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      node = objectMapper.readTree(json);
    } catch (JsonProcessingException e) {
      return jobPostings;
    }

    if (node == null) {
      return jobPostings;
    }

    JsonNode jobNodes = node.get("itemListElement");
    if (!jobNodes.isArray()) {
      return jobPostings;
    }

    for (JsonNode job : jobNodes) {
      JsonNode url = job.get("url");
      if (url == null) {
        continue;
      }
      String href = url.asText();
      JobPosting jobPosting = new JobPosting();
      jobPosting.setHref(href);
      jobPostings.add(jobPosting);
    }

    // do we need to keep scraping
    if (hasMoreResults(document)) {
      this.start += 1;
    }

    return jobPostings;
  }

  private boolean hasMoreResults(Document document) {
    Element next = document.selectFirst("head>link[rel=next]");
    return next != null;
  }

  @Override
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

    // date
    JsonNode datePosted = node.get("datePosted");
    if (datePosted != null) {
      jobPosting.setDate(datePosted.asText());
    }

    // jobTitle
    JsonNode title = node.get("title");
    if (title != null) {
      jobPosting.setJobTitle(title.asText());
    }

    // description
    JsonNode description = node.get("description");
    if (description != null) {
      jobPosting.setDescription(description.asText());
    }

    // tags
    JsonNode tagsNodes = node.get("skills");
    if (tagsNodes != null && tagsNodes.isArray()) {
      ArrayList<String> tagsText = new ArrayList<>();
      for (JsonNode t : tagsNodes) {
        tagsText.add(t.textValue());
      }
      String tags = String.join(" - ", tagsText);
      jobPosting.setTags(tags);
    }

    // company
    JsonNode hiringOrganization = node.get("hiringOrganization");
    if (hiringOrganization != null) {
      JsonNode name = hiringOrganization.get("name");
      if (name != null) {
        jobPosting.setCompany(name.asText());
      }
    }
  }

  @Override
  public ScrapingExecutorType getJobSite() {
    return ScrapingExecutorType.STACKOVERFLOW;
  }


  public URI getNextMainPageURI() {
    try {
      String url = "https://stackoverflow.com/jobs?sort=p&r=true";
      if (start > 1) {
        url = "https://stackoverflow.com/jobs?sort=p&r=true&pg=" + start;
      }
      return new URI(url);
    } catch (URISyntaxException e) {
      return null;
    }
  }


}
