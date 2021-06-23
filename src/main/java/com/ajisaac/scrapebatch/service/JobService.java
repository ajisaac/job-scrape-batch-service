package com.ajisaac.scrapebatch.service;

import com.ajisaac.scrapebatch.dto.*;
import com.google.common.base.Strings;

import javax.inject.Singleton;
import java.util.*;

@Singleton
public class JobService {

  private final DatabaseService db;
  private final FilteringService filteringService;

  public JobService(DatabaseService db, FilteringService filteringService) {
    this.db = db;
    this.filteringService = filteringService;
  }

  public PostingsAndFilter getAllJobs(Filtering filtering) {
    var postings = db.getAllJobPostings();
    postings = filteringService.optimalFiltering(postings, filtering);

    int companyCount = getCompanyCount(postings);
    int jobCount = postings.size();

    if (jobCount > 10)
      postings = postings.subList(0, 10);

    var words = db.getHighlightWords();
    HighlightingWords.highlightJobDescriptions(postings, words);
    PostingsAndFilter f = new PostingsAndFilter();
    f.setNumCompanies(companyCount);
    f.setNumJobs(jobCount);
    f.setPostings(postings);
    f.setFilter(filtering);

    return f;

  }

  private int getCompanyCount(List<JobPosting> postings) {
    Set<String> companies = new HashSet<>();
    for (JobPosting posting : postings) {
      companies.add(posting.getCompany());
    }
    return companies.size();
  }

  public JobPosting updateJobStatus(Long id, String status) {
    var s = Status.getStatusByName(status);
    if (s == null)
      return null;

    return db.updateJobStatus(id, s);

  }

  public List<JobPosting> updateMultipleJobStatuses(List<Long> ids, String status) {
    List<JobPosting> jobPostings = new ArrayList<>();

    for (Long id : ids) {
      var jp = updateJobStatus(id, status);
      if (jp != null)
        jobPostings.add(jp);
    }

    return jobPostings;
  }

  public void addAngelCoJobPosting(JobPosting posting) {
    if (Strings.nullToEmpty(posting.getHref()).isBlank())
      return;

    List<JobPosting> dupeJobs = db.getJobByHref(posting.getHref());

    if (dupeJobs.isEmpty())
      db.storeJobPostingInDatabase(posting);
  }
}