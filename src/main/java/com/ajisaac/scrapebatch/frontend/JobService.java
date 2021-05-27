package com.ajisaac.scrapebatch.frontend;

import com.ajisaac.scrapebatch.dto.*;
import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class JobService {

  private final DatabaseService databaseService;

  @Autowired
  public JobService(DatabaseService databaseService) {
    this.databaseService = databaseService;

  }

  /**
   * Gets all the jobs.
   *
   * @return all the jobs.
   */
  public List<JobPosting> getAllJobs() {
    return databaseService.getAllJobPostings();
  }

  /**
   * Gets all the jobs, but contained within a Company pojo as a list.
   *
   * @return All the companies or an empty list.
   */
  public Companies getAllJobsByCompany() {
    List<JobPosting> jobPostings = databaseService.getAllJobPostings();
    Map<String, List<JobPosting>> companyMap = new HashMap<>();

    // sort by company
    for (JobPosting job : jobPostings) {
      String company = Strings.nullToEmpty(job.getCompany());
      if (company.isBlank()) {
        company = "unknown";
      }
      if (companyMap.containsKey(company)) {
        companyMap.get(company).add(job);
      } else {
        List<JobPosting> list = new ArrayList<>();
        list.add(job);
        companyMap.put(company, list);
      }
    }

    // map the map to list of companies
    long id = 1;
    Companies companies = new Companies();
    for (Map.Entry<String, List<JobPosting>> entrySet : companyMap.entrySet()) {
      Company company = new Company(id++, entrySet.getKey(), entrySet.getValue());
      companies.addCompany(company);
    }

    return companies;
  }

  /**
   * update the given job with a new status
   */
  public JobPosting updateJobStatus(Long id, String status) {
    Status s = Status.getStatusByName(status);
    if (s == null) {
      return null;
    }
    Optional<JobPosting> optionalJobStatus = databaseService.getJobById(id);
    if (optionalJobStatus.isEmpty()) {
      return null;
    }
    JobPosting jobPosting = optionalJobStatus.get();
    jobPosting.setStatus(s.getLowercase());
    jobPosting = databaseService.updateJobPosting(jobPosting);
    return jobPosting;
  }

  /**
   * Update multiple jobs with the status specified
   */
  public List<JobPosting> updateMultipleJobStatuses(List<Long> jobStatuses, String status) {
    List<JobPosting> jobPostings = new ArrayList<>();
    for (Long id : jobStatuses) {
      JobPosting jobPosting = updateJobStatus(id, status);
      if (jobPosting != null) {
        jobPostings.add(jobPosting);
      }
    }
    return jobPostings;
  }

  /**
   * Get all the blacklisted companies
   */
  public List<String> getBlacklistedCompanies() {
    return databaseService.getAllBlacklistedCompanies();
  }

  public BlacklistedCompany addBlacklistedCompany(BlacklistedCompany blc) {
    return databaseService.addBlacklistedCompany(blc);
  }

  public void deleteBlacklistedCompany(BlacklistedCompany blc) {
    databaseService.removeBlacklistedCompany(blc);
  }

  public void addAngelCoJobPosting(JobPosting posting) {
    // do some validation on the body for sanity
    if (Strings.nullToEmpty(posting.getHref()).isBlank()) {
      return;
    }

    var location = posting.getLocation();
    if(!Strings.nullToEmpty(location).isBlank()){
      location = addDashes(location);
      posting.setLocation(location);
    }

    var tags = posting.getTags();
    if(!Strings.nullToEmpty(tags).isBlank()){
      tags = addDashes(tags);
      posting.setTags(tags);
    }

    var remote = posting.getRemoteText();
    if(!Strings.nullToEmpty(remote).isBlank()){
      remote = addDashes(remote);
      posting.setRemoteText(remote);
    }

    List<JobPosting> dupeJobs = databaseService.getJobByHref(posting.getHref());
    if(dupeJobs.isEmpty()){
      databaseService.storeJobPostingInDatabase(posting);
    }
  }

  // todo not sure if needed
  private String addDashes(String location) {
    return location;
  }
}