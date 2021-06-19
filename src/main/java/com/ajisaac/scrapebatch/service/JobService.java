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

    highlightJobDescriptions(postings);
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

  private void highlightJobDescriptions(List<JobPosting> postings) {
    List<HighlightWord> buzzwords = db.getHighlightWords();

    buzzwords.sort((word1, word2) -> {
      var w1 = word1.getName().toLowerCase(Locale.ROOT);
      var w2 = word2.getName().toLowerCase(Locale.ROOT);

      if (w1.equals(w2))
        return 0;

      if (w1.length() < w2.length() && w2.startsWith(w1))
        return 1;
      if (w2.length() < w1.length() && w1.startsWith(w2))
        return -1;

      return w1.compareTo(w2);

    });

    for (JobPosting jp : postings) {
      var desc = jp.getDescription();
      desc = highlightJobDescription(desc, buzzwords);
      jp.setDescription(desc);
    }
  }

  private String highlightJobDescription(String desc, List<HighlightWord> buzzwords) {
    if (desc == null || buzzwords == null || buzzwords.isEmpty())
      return desc;


    for (HighlightWord buzzword : buzzwords) {
      // todo make this much better
      // todo maybe apply this only when updating highlight words
      // really want to hit the edge cases
      var word = buzzword.getName();
      var replaceWord = " <span class=\"highlight\">" + word + "</span>";
      desc = desc.replaceAll("(?i)" + " " + word, replaceWord);
    }

    return desc;
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