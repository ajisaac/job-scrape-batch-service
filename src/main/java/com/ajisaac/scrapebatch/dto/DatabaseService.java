package com.ajisaac.scrapebatch.dto;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Singleton;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

//@Singleton
@ApplicationScoped
public class DatabaseService {
  private final JobPostingRepository jobPostingRepository;
  private final ScrapeJobRepository scrapeJobRepository;
  private final HighlightWordRepository highlightWordsRepository;

  public DatabaseService(
    JobPostingRepository jobPostingRepository,
    ScrapeJobRepository scrapeJobRepository,
    HighlightWordRepository highlightWordsRepository) {
    this.jobPostingRepository = jobPostingRepository;
    this.scrapeJobRepository = scrapeJobRepository;
    this.highlightWordsRepository = highlightWordsRepository;
  }

  public List<JobPosting> getAllJobPostings() {
    return jobPostingRepository.findAll().list();
  }

  public JobPosting getJobById(Long id) {
    return jobPostingRepository.findById(id);
  }

  @Transactional
  public JobPosting updateJobPosting(JobPosting jp) {
    if (jp != null)
      jobPostingRepository.persist(jp);
    return jp;
  }

  @Transactional
  public void storeJobPostingInDatabase(JobPosting jp) {
    if (jp != null)
      jobPostingRepository.persist(jp);
  }

  @Transactional
  public ScrapeJob storeScrapeJobInDatabase(ScrapeJob sj) {
    if (sj != null)
      scrapeJobRepository.persist(sj);
    return sj;
  }

  public ScrapeJob getScrapeJobById(long idNum) {
    return scrapeJobRepository.findById(idNum);
  }

  public List<ScrapeJob> getAllScrapeJobs() {
    return scrapeJobRepository.findAll().list();
  }

  public List<JobPosting> getJobByHref(String href) {
    return jobPostingRepository.findAllByHref(href);
  }

  public List<String> getHrefsForSite(String site) {
    var postings = jobPostingRepository.findAllByJobSite(site);
    return postings.stream()
      .map(JobPosting::getHref)
      .collect(Collectors.toList());
  }

  public List<HighlightWord> getHighlightWords() {
    return this.highlightWordsRepository.findAll().list();
  }
}
