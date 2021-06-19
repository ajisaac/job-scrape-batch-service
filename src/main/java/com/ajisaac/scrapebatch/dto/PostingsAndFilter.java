package com.ajisaac.scrapebatch.dto;

import java.util.List;

public class PostingsAndFilter {
  private List<JobPosting> postings;
  private Filtering filter;
  private int numJobs;
  private int numCompanies;


  public int getNumCompanies() {
    return numCompanies;
  }

  public int getNumJobs() {
    return numJobs;
  }

  public void setNumCompanies(int numCompanies) {
    this.numCompanies = numCompanies;
  }

  public void setNumJobs(int numJobs) {
    this.numJobs = numJobs;
  }

  public List<JobPosting> getPostings() {
    return postings;
  }

  public void setPostings(List<JobPosting> postings) {
    this.postings = postings;
  }

  public Filtering getFilter() {
    return filter;
  }

  public void setFilter(Filtering filter) {
    this.filter = filter;
  }
}
