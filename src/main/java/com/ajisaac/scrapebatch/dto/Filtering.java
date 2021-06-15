package com.ajisaac.scrapebatch.dto;

import java.util.List;
import java.util.Map;

/**
 * Explains how to filter a list of JobPostings
 */
public class Filtering {

  // any particular set of job statuses
  private Map<String, Boolean> statuses;
  // any particular set of job sites
  private Map<String, Boolean> jobSites;
  // company name to filter by
  private String company;
  // texts to search for in job description
  private String jobDescriptionText;
  // texts to search for in job title
  private String jobTitleText;

  public Map<String, Boolean> getStatuses() {
    return statuses;
  }

  public void setStatuses(Map<String, Boolean> statuses) {
    this.statuses = statuses;
  }

  public Map<String, Boolean> getJobSites() {
    return jobSites;
  }

  public void setJobSites(Map<String, Boolean> jobSites) {
    this.jobSites = jobSites;
  }

  public String getCompany() {
    return company;
  }

  public void setCompany(String company) {
    this.company = company;
  }

  public String getJobDescriptionText() {
    return jobDescriptionText == null ? "" : jobDescriptionText;
  }

  public void setJobDescriptionText(String jobDescriptionText) {
    this.jobDescriptionText = jobDescriptionText;
  }

  public String getJobTitleText() {
    return jobTitleText == null ? "" : jobTitleText;
  }

  public void setJobTitleText(String jobTitleText) {
    this.jobTitleText = jobTitleText;
  }
}
