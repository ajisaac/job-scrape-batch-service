package com.ajisaac.scrapebatch.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Type;

import javax.persistence.*;

/**
 * Represents a single job posting. Any or all of these fields might just not exist, depending upon
 * the job site we scraped or just errors we had scraping that job site. Be warned.
 */
@Entity
public final class JobPosting {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @JsonIgnoreProperties(ignoreUnknown = true)
  private long id;

  private String jobTitle;
  private String tags;
  @Column(columnDefinition = "TEXT")
  private String href;
  @Column(columnDefinition = "TEXT")
  private String summary;
  private String company;
  private String location;
  private String date;
  private String salary;
  private String jobSite;
  @Column(columnDefinition = "TEXT")
  private String description;
  private String type;
  private String remoteText;
  @Column(columnDefinition = "TEXT")
  private String miscText;
  private String status;

  @JsonIgnore
  private boolean ignoreScrapeDescriptionPage;

  public boolean isIgnoreScrapeDescriptionPage() {
    return ignoreScrapeDescriptionPage;
  }

  public void setIgnoreScrapeDescriptionPage(boolean ignoreScrapeDescriptionPage) {
    this.ignoreScrapeDescriptionPage = ignoreScrapeDescriptionPage;
  }

  public JobPosting() {}

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getJobTitle() {
    return jobTitle;
  }

  public void setJobTitle(String jobTitle) {
    this.jobTitle = jobTitle;
  }

  public String getTags() {
    return tags;
  }

  public void setTags(String tags) {
    this.tags = tags;
  }

  public String getHref() {
    return href;
  }

  public void setHref(String href) {
    this.href = href;
  }

  public String getSummary() {
    return summary;
  }

  public void setSummary(String summary) {
    this.summary = summary;
  }

  public String getCompany() {
    return company;
  }

  public void setCompany(String company) {
    this.company = company;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public String getSalary() {
    return salary;
  }

  public void setSalary(String salary) {
    this.salary = salary;
  }

  public String getJobSite() {
    return jobSite;
  }

  public void setJobSite(String jobSite) {
    this.jobSite = jobSite;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getRemoteText() {
    return remoteText;
  }

  public void setRemoteText(String remoteText) {
    this.remoteText = remoteText;
  }

  public String getMiscText() {
    return miscText;
  }

  public void setMiscText(String miscText) {
    this.miscText = miscText;
  }
}
