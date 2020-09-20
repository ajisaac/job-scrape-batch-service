package com.ajisaac.scrapebatch.dto;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class Company {
  private final Long id;
  private final String name;
  private final List<JobPosting> jobPostings;

  /**
   * Creates a company with all it's job listings.
   *
   * @param name The name of the company.
   * @param jobPostings A list of JobPostings;
   */
  public Company(Long id, String name, List<JobPosting> jobPostings) {
    checkNotNull(name);
    checkNotNull(jobPostings);

    this.id = id;
    this.name = name;
    this.jobPostings = jobPostings;
  }

  public Long getId() {
    return id;
  }

  public List<JobPosting> getJobPostings() {
    return jobPostings;
  }

  public String getName() {
    return name;
  }
}
