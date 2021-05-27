package com.ajisaac.scrapebatch.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

@Service
public final class DatabaseService {
  private final JobPostingRepository jobPostingRepository;
  private final ScrapeJobRepository scrapeJobRepository;

  @Autowired
  public DatabaseService(
    JobPostingRepository jobPostingRepository,
    ScrapeJobRepository scrapeJobRepository) {
    this.jobPostingRepository = jobPostingRepository;
    this.scrapeJobRepository = scrapeJobRepository;
  }

  public List<JobPosting> getAllJobPostings() {
    return jobPostingRepository.findAll();
  }

  public Optional<JobPosting> getJobById(Long id) {
    return jobPostingRepository.findById(id);
  }

  public JobPosting updateJobPosting(JobPosting jobPosting) {
    if (jobPosting == null)
      return null;

    return jobPostingRepository.save(jobPosting);
  }


  public void storeJobPostingInDatabase(JobPosting jobPosting) {
    if (jobPosting == null)
      return;
    jobPostingRepository.save(jobPosting);
  }

  public ScrapeJob storeScrapeJobInDatabase(ScrapeJob scrapeJob) {
    if(scrapeJob == null)
      return null;

    checkNotNull(scrapeJob, "Can't save null scrapeJob.");
    scrapeJob = scrapeJobRepository.save(scrapeJob);
    return scrapeJob;
  }

  public ScrapeJob getScrapeJobById(long idNum) {
    return scrapeJobRepository.findById(idNum).orElse(null);
  }

  public List<ScrapeJob> getAllScrapeJobs() {
    return scrapeJobRepository.findAll();
  }

  public List<JobPosting> getJobByHref(String href) {
    return jobPostingRepository.findAllByHref(href);
  }
}
