package com.ajisaac.scrapebatch.dto;

import java.util.List;

/**
 * Explains how to filter a list of JobPostings
 */
public class Filtering {

  // any particular set of job statuses
  private List<String> statuses;
  // any particular set of job sites
  private List<String> jobSites;
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
  // number of elements
  private Integer limit;

  public List<String> getStatuses() {
    return statuses;
  }

  public void setStatuses(List<String> statuses) {
    this.statuses = statuses;
  }

  public List<String> getJobSites() {
    return jobSites;
  }

  public void setJobSites(List<String> jobSites) {
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

  public Integer getLimit() {
    return limit;
  }

  public void setLimit(Integer limit) {
    this.limit = limit;
  }
}
