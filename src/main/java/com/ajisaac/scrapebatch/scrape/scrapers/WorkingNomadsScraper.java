package com.ajisaac.scrapebatch.scrape.scrapers;

import com.ajisaac.scrapebatch.dto.DatabaseService;
import com.ajisaac.scrapebatch.dto.JobPosting;
import com.ajisaac.scrapebatch.dto.ScrapeJob;
import com.ajisaac.scrapebatch.scrape.ScrapingExecutorType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.utils.URIBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class WorkingNomadsScraper implements Scraper {
  private final ScrapeJob scrapeJob;

  public WorkingNomadsScraper(ScrapeJob scrapeJob) {
    this.scrapeJob = scrapeJob;
  }

  public List<JobPosting> parseMainPage(String mainPage) {
    final List<JobPosting> jobPostings = new ArrayList<>();
    // parse the page for jobs
    // they give us an API for convenience
    JsonNode nodes;
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      nodes = objectMapper.readTree(mainPage);
    } catch (JsonProcessingException e) {
      return jobPostings;
    }

    if (nodes == null) {
      return jobPostings;
    }

    if (!nodes.isArray()) {
      return jobPostings;
    }

    for (JsonNode job : nodes) {
      JobPosting jobPosting = new JobPosting();
      jobPosting.setIgnoreScrapeDescriptionPage(true);

      // url
      JsonNode url = job.get("url");
      if (url == null) {
        continue;
      }
      jobPosting.setHref(url.asText());

      // category_name
      JsonNode category = job.get("category_name");
      if (category == null || (!category.asText().equals("Development"))) {
        continue;
      }

      // title
      JsonNode title = job.get("title");
      if (title != null) {
        jobPosting.setJobTitle(title.asText());
      }

      // description
      JsonNode description = job.get("description");
      if (description != null) {
        jobPosting.setDescription(description.asText());
      } else {
        jobPosting.setDescription("");
      }

      // company_name
      JsonNode companyName = job.get("company_name");
      if (companyName != null) {
        jobPosting.setCompany(companyName.asText());
      } else {
        jobPosting.setCompany("unknown");
      }

      // tags
      JsonNode tags = job.get("tags");
      if (tags != null) {
        String[] tagsArr = tags.asText().split(",");
        String tagsFinal = String.join(" - ", tagsArr);
        jobPosting.setTags(tagsFinal);
      }

      // location
      JsonNode location = job.get("location");
      if (location != null) {
        jobPosting.setLocation(location.asText());
      }

      // pub_date
      JsonNode pubDate = job.get("pub_date");
      if (pubDate != null) {
        jobPosting.setDate(pubDate.asText());
      }

      jobPostings.add(jobPosting);
    }

    return jobPostings;
  }

  public void parseJobDescriptionPage(String jobDescriptionPage, JobPosting jobPosting) {
  }

  public ScrapingExecutorType getJobSite() {
    return ScrapingExecutorType.WORKINGNOMADS;
  }

  public URI getNextMainPageURI() {
    var uriBuilder = new URIBuilder();
    try {
      // default values
      uriBuilder.setScheme("https");
      uriBuilder.setHost("www.workingnomads.co");
      uriBuilder.setPath("api/exposed_jobs/");
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
