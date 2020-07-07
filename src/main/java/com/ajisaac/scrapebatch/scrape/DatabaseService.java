package com.ajisaac.scrapebatch.scrape;

import com.ajisaac.scrapebatch.dto.JobPostingRepository;
import com.ajisaac.scrapebatch.dto.ScrapeJob;
import com.ajisaac.scrapebatch.dto.ScrapeJobRepository;
import com.ajisaac.scrapebatch.dto.JobPosting;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

@Service
public class DatabaseService {

  private final ScrapeJobRepository scrapeJobRepository;
  private final JobPostingRepository jobPostingRepository;

  public DatabaseService(
      ScrapeJobRepository scrapeJobRepository, JobPostingRepository jobPostingRepository) {
    this.scrapeJobRepository = scrapeJobRepository;
    this.jobPostingRepository = jobPostingRepository;
  }

  public JobPosting storeJobPostingInDatabase(JobPosting jobPosting) {
    checkNotNull(jobPosting, "Can't save null jobPosting.");
    jobPosting = jobPostingRepository.save(jobPosting);
    return jobPosting;
  }

  public ScrapeJob storeScrapeJobInDatabase(ScrapeJob scrapeJob) {
    checkNotNull(scrapeJob, "Can't save null scrapeJob.");
    scrapeJob = scrapeJobRepository.save(scrapeJob);
    return scrapeJob;
  }

  public Optional<ScrapeJob> getScrapeJobById(long idNum) {
    return scrapeJobRepository.findById(idNum);
  }

  public List<ScrapeJob> getAllScrapeJobs() {
    return scrapeJobRepository.findAll();

  }
}
