package com.ajisaac.scrapebatch.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

@Service
public final class DatabaseService {
  private final BlacklistedCompanyRepository blacklistedCompanyRepository;
  private final BlockTitleRepository blockTitleRepository;
  private final JobPostingRepository jobPostingRepository;
  private final ScrapeJobRepository scrapeJobRepository;

  @Autowired
  public DatabaseService(
      BlacklistedCompanyRepository blacklistedCompanyRepository,
      BlockTitleRepository blockTitleRepository,
      JobPostingRepository jobPostingRepository,
      ScrapeJobRepository scrapeJobRepository) {
    this.blacklistedCompanyRepository = blacklistedCompanyRepository;
    this.blockTitleRepository = blockTitleRepository;
    this.jobPostingRepository = jobPostingRepository;
    this.scrapeJobRepository = scrapeJobRepository;
  }

  /**
   * Get all the job postings.
   *
   * @return a list of all the JobPostings.
   */
  public List<JobPosting> getAllJobPostings() {
    return jobPostingRepository.findAll();
  }

  /**
   * Get all the blacklisted companies.
   *
   * @return a list of the BlacklistedCompanies.
   */
  public List<String> getAllBlacklistedCompanies() {
    List<BlacklistedCompany> blcs = blacklistedCompanyRepository.findAll();
    List<String> companies = new ArrayList<>();
    for (BlacklistedCompany blc : blcs) {
      companies.add(blc.getName());
    }
    return companies;
  }

  /**
   * Get all the blocked titles.
   *
   * @return a list of the blocked titles.
   */
  public List<BlockTitle> getAllBlockedTitles() {
    return blockTitleRepository.findAll();
  }

  public Optional<JobPosting> getJobById(Long id) {
    return jobPostingRepository.findById(id);
  }

  /**
   * Updates the job posting with whatever data it currently has.
   *
   * @param jobPosting The job posting to update.
   * @return The updated job posting or null if jobPosting was null.
   */
  public JobPosting updateJobPosting(JobPosting jobPosting) {
    if (jobPosting == null) {
      return null;
    }
    return jobPostingRepository.save(jobPosting);
  }

  public BlacklistedCompany addBlacklistedCompany(BlacklistedCompany blc) {
    return blacklistedCompanyRepository.save(blc);
  }

  public void removeBlacklistedCompany(BlacklistedCompany blc) {
    List<BlacklistedCompany> blcs = blacklistedCompanyRepository.findByName(blc.getName());
    for (BlacklistedCompany b : blcs) {
      blacklistedCompanyRepository.delete(b);
    }
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
