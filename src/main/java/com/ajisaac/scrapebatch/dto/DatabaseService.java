package com.ajisaac.scrapebatch.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

@Service
public final class DatabaseService {
  private final JobPostingRepository jobPostingRepository;
  private final ScrapeJobRepository scrapeJobRepository;
  private final HighlightWordRepository highlightWordsRepository;

  @Autowired
  public DatabaseService(
    JobPostingRepository jobPostingRepository,
    ScrapeJobRepository scrapeJobRepository,
    HighlightWordRepository highlightWordsRepository) {
    this.jobPostingRepository = jobPostingRepository;
    this.scrapeJobRepository = scrapeJobRepository;
    this.highlightWordsRepository = highlightWordsRepository;
  }

  public List<JobPosting> getAllJobPostings() {
    return jobPostingRepository.findAll();
  }

  public JobPosting getJobById(Long id) {
    return jobPostingRepository.findById(id).orElse(null);
  }

  public JobPosting updateJobPosting(JobPosting jp) {
    if (jp == null)
      return null;
    return jobPostingRepository.save(jp);
  }


  public void storeJobPostingInDatabase(JobPosting jp) {
    if (jp == null)
      return;
    jobPostingRepository.save(jp);
  }

  public ScrapeJob storeScrapeJobInDatabase(ScrapeJob sj) {
    if (sj == null)
      return null;
    return scrapeJobRepository.save(sj);
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

  public List<String> getHrefsForSite(String site) {
    var postings = jobPostingRepository.findAllByJobSite(site);
    return postings.stream()
      .map(JobPosting::getHref)
      .collect(Collectors.toList());
  }

  public List<HighlightWord> getHighlightWords() {
    return this.highlightWordsRepository.findAll();
  }
}
