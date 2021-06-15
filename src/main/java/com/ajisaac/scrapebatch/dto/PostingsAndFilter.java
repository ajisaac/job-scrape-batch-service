package com.ajisaac.scrapebatch.dto;

import java.util.List;

public class PostingsAndFilter {
  private List<JobPosting> postings;
  private Filtering filter;

  public PostingsAndFilter(List<JobPosting> postings, Filtering filtering) {
    this.postings = postings;
    this.filter = filtering;

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
