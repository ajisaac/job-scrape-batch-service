package com.ajisaac.scrapebatch.frontend;

import com.ajisaac.scrapebatch.dto.*;
import com.google.common.base.Strings;

import javax.inject.Singleton;
import java.util.*;

@Singleton
public class JobService {

  private final DatabaseService db;

  public JobService(DatabaseService db) {
    this.db = db;

  }

  public List<JobPosting> getAllJobs() {
    return db.getAllJobPostings();
  }

  public Companies getAllJobsByCompany() {
    Map<String, List<JobPosting>> cMap = new HashMap<>();

    for (JobPosting job : db.getAllJobPostings()) {
      var c = Strings.nullToEmpty(job.getCompany());

      if (c.isBlank())
        c = "unknown";

      if (cMap.containsKey(c)) {
        cMap.get(c).add(job);
      } else {
        List<JobPosting> list = new ArrayList<>();
        list.add(job);
        cMap.put(c, list);
      }
    }

    // map the map to list of companies
    long id = 1;
    var companies = new Companies();
    for (Map.Entry<String, List<JobPosting>> es : cMap.entrySet()) {
      var company = new Company(id++, es.getKey(), es.getValue());
      companies.addCompany(company);
    }

    highlightJobDescriptions(companies);

    return companies;
  }

  private void highlightJobDescriptions(Companies companies) {
    List<HighlightWord> buzzwords = db.getHighlightWords();
    for (var c : companies.getCompanies()) {
      for (JobPosting jp : c.getJobPostings()) {
        var desc = jp.getDescription();
        desc = highlightJobDescription(desc, buzzwords);
        jp.setDescription(desc);
      }
    }
  }

  private String highlightJobDescription(String desc, List<HighlightWord> buzzwords) {
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