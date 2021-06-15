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
  // do we filter out blacklisted
  private boolean filterBlacklist;
  // do we filter out graylisted
  private boolean filterGraylist;
  // company name to filter by
  private String company;
  // texts to search for in job description
  private List<String> jobDescriptionTexts;
  // texts to search for in job title
  private List<String> jobTitleTexts;

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

  public boolean isFilterBlacklist() {
    return filterBlacklist;
  }

  public void setFilterBlacklist(boolean filterBlacklist) {
    this.filterBlacklist = filterBlacklist;
  }

  public boolean isFilterGraylist() {
    return filterGraylist;
  }

  public void setFilterGraylist(boolean filterGraylist) {
    this.filterGraylist = filterGraylist;
  }

  public String getCompany() {
    return company;
  }

  public void setCompany(String company) {
    this.company = company;
  }

  public List<String> getJobDescriptionTexts() {
    return jobDescriptionTexts;
  }

  public void setJobDescriptionTexts(List<String> jobDescriptionTexts) {
    this.jobDescriptionTexts = jobDescriptionTexts;
  }

  public List<String> getJobTitleTexts() {
    return jobTitleTexts;
  }

  public void setJobTitleTexts(List<String> jobTitleTexts) {
    this.jobTitleTexts = jobTitleTexts;
  }

}
