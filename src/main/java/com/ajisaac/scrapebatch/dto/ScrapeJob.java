package com.ajisaac.scrapebatch.dto;

import com.ajisaac.scrapebatch.scrape.ScrapingExecutorType;
import com.ajisaac.scrapebatch.scrape.executors.MultiPageScrapingExecutor;
import com.ajisaac.scrapebatch.scrape.executors.ScrapingExecutor;
import com.ajisaac.scrapebatch.scrape.executors.SinglePageScrapingExecutor;
import com.ajisaac.scrapebatch.scrape.scrapers.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Objects;

/**
 * One of these holds all the data needed to handle a single scraping.
 */
@Entity
public class ScrapeJob {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @JsonIgnoreProperties(ignoreUnknown = true)
  private long id;

  private String site;
  private String name;
  private String query;
  private String location;
  private boolean remote;
  private int radius;
  private String jobType;
  private String sortType;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getQuery() {
    return query;
  }

  public void setQuery(String query) {
    this.query = query;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public boolean isRemote() {
    return remote;
  }

  public void setRemote(boolean remote) {
    this.remote = remote;
  }

  public int getRadius() {
    return radius;
  }

  public void setRadius(int radius) {
    this.radius = radius;
  }

  public String getJobType() {
    return jobType;
  }

  public void setJobType(String jobType) {
    this.jobType = jobType;
  }

  public String getSortType() {
    return sortType;
  }

  public void setSortType(String sortType) {
    this.sortType = sortType;
  }

  public String getSite() {
    return this.site;
  }

  public void setSite(String site) {
    this.site = site;
  }

  /**
   * weak compare using name and site
   */
  public boolean weakEquals(ScrapeJob sj) {
    return Objects.equals(site, sj.site) && Objects.equals(name, sj.name);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ScrapeJob scrapeJob = (ScrapeJob) o;
    return id == scrapeJob.id
      && remote == scrapeJob.remote
      && radius == scrapeJob.radius
      && Objects.equals(site, scrapeJob.site)
      && Objects.equals(name, scrapeJob.name)
      && Objects.equals(query, scrapeJob.query)
      && Objects.equals(location, scrapeJob.location)
      && Objects.equals(jobType, scrapeJob.jobType)
      && Objects.equals(sortType, scrapeJob.sortType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, site, name, query, location, remote, radius, jobType, sortType);
  }

  public ScrapingExecutor getExecutor() {
    ScrapingExecutorType type = getTypeFromScrapeJob();
    if (type == null) {
      return null;
    }
    switch (type) {
      case INDEED -> {
        IndeedScraper indeedScraper = new IndeedScraper(this);
        return new MultiPageScrapingExecutor(indeedScraper);
      }
      case WWR -> {
        Scraper wwrScraper = new WwrScraper(this);
        return new SinglePageScrapingExecutor(wwrScraper);
      }
      case REMOTIVEIO -> {
        Scraper remoteivioScraper = new RemoteivioScraper(this);
        return new SinglePageScrapingExecutor(remoteivioScraper);
      }
      case REMOTECO -> {
        Scraper remotecoScraper = new RemotecoScraper(this);
        return new SinglePageScrapingExecutor(remotecoScraper);
      }
      case REMOTEOKIO -> {
        Scraper remoteokioScraper = new RemoteokioScraper(this);
        return new SinglePageScrapingExecutor(remoteokioScraper);
      }
      case SITEPOINT -> {
        Scraper sitepointScraper = new SitepointScraper(this);
        return new MultiPageScrapingExecutor(sitepointScraper);
      }
      case STACKOVERFLOW -> {
        Scraper stackoverflowScraper = new StackoverflowScraper(this);
        return new MultiPageScrapingExecutor(stackoverflowScraper);
      }
      case WORKINGNOMADS -> {
        Scraper workingnomadsScraper = new WorkingNomadsScraper(this);
        return new SinglePageScrapingExecutor(workingnomadsScraper);
      }
    }
    return null;
  }

  public ScrapingExecutorType getTypeFromScrapeJob() {
    try {
      return ScrapingExecutorType.valueOf(this.site);
    } catch (IllegalArgumentException ex) {
      return null;
    }
  }
}
