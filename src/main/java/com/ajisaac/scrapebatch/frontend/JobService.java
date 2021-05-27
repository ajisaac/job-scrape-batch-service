package com.ajisaac.scrapebatch.frontend;

import com.ajisaac.scrapebatch.dto.*;
import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class JobService {

  private final DatabaseService db;

  @Autowired
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

    return companies;
  }

  public JobPosting updateJobStatus(Long id, String status) {
    var s = Status.getStatusByName(status);
    if (s == null)
      return null;

    var optionalJobStatus = db.getJobById(id);
    if (optionalJobStatus == null)
      return null;

    var jobPosting = optionalJobStatus;
    jobPosting.setStatus(s.getLowercase());
    jobPosting = db.updateJobPosting(jobPosting);
    return jobPosting;
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