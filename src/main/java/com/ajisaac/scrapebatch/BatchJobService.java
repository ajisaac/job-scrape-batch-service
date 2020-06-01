package com.ajisaac.scrapebatch;

import com.ajisaac.scrapebatch.indeed.IndeedSearch;
import com.ajisaac.scrapebatch.indeed.IndeedSearchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** Business logic for handling batch jobs, this is our singleton manager class */
@Service
public class BatchJobService {

  private final IndeedSearchRepository repository;
  // takes our job and stores it in the data store

  @Autowired
  public BatchJobService(IndeedSearchRepository repository) {
    this.repository = repository;
  }

  /**
   * Submits a job to the database.
   */
  public IndeedSearch submitIndeedScrapeJob(IndeedSearch job) {
    job = repository.save(job);
    return job;
  }
}
