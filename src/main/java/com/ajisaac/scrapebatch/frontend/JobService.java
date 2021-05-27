package com.ajisaac.scrapebatch.frontend;

import com.ajisaac.scrapebatch.dto.*;
import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class JobService {

  private final DatabaseService dbService;

  @Autowired
  public JobService(DatabaseService dbService) {
    this.dbService = dbService;

  }

  public List<JobPosting> getAllJobs() {
    return dbService.getAllJobPostings();
  }

  public Companies getAllJobsByCompany() {
    List<JobPosting> jobPostings = dbService.getAllJobPostings();
    Map<String, List<JobPosting>> cMap = new HashMap<>();

    // sort by company
    for (JobPosting job : jobPostings) {
      var company = Strings.nullToEmpty(job.getCompany());
      if (company.isBlank()) {
        company = "unknown";
      }
      if (cMap.containsKey(company)) {
        cMap.get(company).add(job);
      } else {
        List<JobPosting> list = new ArrayList<>();
        list.add(job);
        cMap.put(company, list);
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

  /**
   * update the given job with a new status
   */
  public JobPosting updateJobStatus(Long id, String status) {
    var s = Status.getStatusByName(status);
    if (s == null) {
      return null;
    }
    Optional<JobPosting> optionalJobStatus = dbService.getJobById(id);
    if (optionalJobStatus.isEmpty()) {
      return null;
    }
    var jobPosting = optionalJobStatus.get();
    jobPosting.setStatus(s.getLowercase());
    jobPosting = dbService.updateJobPosting(jobPosting);
    return jobPosting;
  }

  public List<JobPosting> updateMultipleJobStatuses(List<Long> ids, String status) {
    List<JobPosting> jobPostings = new ArrayList<>();
    for (Long id : ids) {
      var jp = updateJobStatus(id, status);
      if (jp != null) {
        jobPostings.add(jp);
      }
    }
    return jobPostings;
  }

  public List<String> getBlacklistedCompanies() {
    return dbService.getAllBlacklistedCompanies();
  }

  public BlacklistedCompany addBlacklistedCompany(BlacklistedCompany blc) {
    return dbService.addBlacklistedCompany(blc);
  }

  public void deleteBlacklistedCompany(BlacklistedCompany blc) {
    dbService.removeBlacklistedCompany(blc);
  }

  public void addAngelCoJobPosting(JobPosting posting) {
    // do some validation on the body for sanity
    if (Strings.nullToEmpty(posting.getHref()).isBlank()) {
      return;
    }

    var location = posting.getLocation();
    if (!Strings.nullToEmpty(location).isBlank()) {
      location = addDashes(location);
      posting.setLocation(location);
    }

    var tags = posting.getTags();
    if (!Strings.nullToEmpty(tags).isBlank()) {
      tags = addDashes(tags);
      posting.setTags(tags);
    }

    var remote = posting.getRemoteText();
    if (!Strings.nullToEmpty(remote).isBlank()) {
      remote = addDashes(remote);
      posting.setRemoteText(remote);
    }

    List<JobPosting> dupeJobs = dbService.getJobByHref(posting.getHref());
    if (dupeJobs.isEmpty()) {
      dbService.storeJobPostingInDatabase(posting);
    }
  }

  // todo not sure if needed
  private String addDashes(String location) {
    return location;
  }
}