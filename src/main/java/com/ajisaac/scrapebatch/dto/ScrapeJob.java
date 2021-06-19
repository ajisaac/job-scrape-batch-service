package com.ajisaac.scrapebatch.dto;

import com.ajisaac.scrapebatch.scrape.ScrapingExecutorType;
import com.ajisaac.scrapebatch.scrape.executors.MultiPageScrapingExecutor;
import com.ajisaac.scrapebatch.scrape.executors.ScrapingExecutor;
import com.ajisaac.scrapebatch.scrape.executors.SinglePageScrapingExecutor;
import com.ajisaac.scrapebatch.scrape.scrapers.*;
import com.ajisaac.scrapebatch.scrape.scrapers.unused.IndeedScraper;
import com.ajisaac.scrapebatch.scrape.scrapers.unused.RemotiveioScraper;
import com.ajisaac.scrapebatch.scrape.scrapers.unused.SitepointScraper;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

  // only used by some scrapers
  private String url;

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

  public void setUrl(String url) {
    this.url = url;
  }

  public String getUrl() {
    return url;
  }

  /**
   * weak compare using name and site
   */
  public boolean weakEquals(ScrapeJob sj) {
    return Objects.equals(site, sj.site)
      && Objects.equals(name, sj.name)
      && Objects.equals(url, sj.url);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    var scrapeJob = (ScrapeJob) o;
    return id == scrapeJob.id
      && remote == scrapeJob.remote
      && radius == scrapeJob.radius
      && Objects.equals(site, scrapeJob.site)
      && Objects.equals(name, scrapeJob.name)
      && Objects.equals(query, scrapeJob.query)
      && Objects.equals(location, scrapeJob.location)
      && Objects.equals(jobType, scrapeJob.jobType)
      && Objects.equals(url, scrapeJob.url)
      && Objects.equals(sortType, scrapeJob.sortType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, site, name, query, location, remote, radius, jobType, sortType, url);
  }

  @JsonIgnore
  public ScrapingExecutor getExecutor() {
    ScrapingExecutorType type = getTypeFromScrapeJob();
    if (type == null) {
      return null;
    }
    switch (type) {
      case INDEED:
        return new MultiPageScrapingExecutor(new IndeedScraper(this));
      case WWR:
        return new SinglePageScrapingExecutor(new WwrScraper(this));
      case REMOTIVEIO:
        return new SinglePageScrapingExecutor(new RemotiveioScraper(this));
      case REMOTECO:
        return new SinglePageScrapingExecutor(new RemotecoScraper(this));
      case REMOTEOKIO:
        return new SinglePageScrapingExecutor(new RemoteokioScraper(this));
      case SITEPOINT:
        return new MultiPageScrapingExecutor(new SitepointScraper(this));
      case STACKOVERFLOW:
        return new MultiPageScrapingExecutor(new StackoverflowScraper(this));
      case WORKINGNOMADS:
        return new SinglePageScrapingExecutor(new WorkingNomadsScraper(this));
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
