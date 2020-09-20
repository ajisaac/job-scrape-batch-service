package com.ajisaac.scrapebatch;

import com.ajisaac.scrapebatch.dto.DatabaseService;
import com.ajisaac.scrapebatch.dto.ScrapeJob;
import com.ajisaac.scrapebatch.scrape.ScrapingExecutorType;
import com.ajisaac.scrapebatch.scrape.ScrapingExecutor;
import com.ajisaac.scrapebatch.webservice.Message;
import com.ajisaac.scrapebatch.webservice.GenericMessage;
import com.ajisaac.scrapebatch.webservice.ScrapeJobMessage;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;

import static com.google.common.base.Preconditions.checkNotNull;

/** Business logic for handling batch jobs, this is our singleton manager class */
@Service
public class BatchJobService {

  // takes our job and stores it in the data store
  DatabaseService databaseService;

  // this is how we will keep track of currently scraped jobs
  private List<ScrapingExecutorType> jobsInProgress =
      Collections.synchronizedList(new ArrayList<>());

  private final ListeningExecutorService executorService =
      MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(10));

  @Autowired
  public BatchJobService(DatabaseService databaseService) {
    this.databaseService = databaseService;
  }

  /**
   * Are we currently scraping this job site.
   *
   * @param idNum The id of a particular batch scrape job.
   * @return true if we are scraping, else false.
   */
  public boolean isCurrentlyScraping(long idNum) {
    var s = databaseService.getScrapeJobById(idNum);
    if (s.isPresent()) {
      var type = ScrapingExecutorType.GetTypeFromScrapeJob(s.get());
      var jobs = ImmutableList.copyOf(this.jobsInProgress);

      for (var j : jobs) {
        if (j == type) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Tells us to start scraping this job site.
   *
   * @param idNum The id of the batch job.
   */
  public Optional<Message> doScrape(long idNum) {

    // we're given this isNum, which relates to a particular batch job
    Optional<ScrapeJob> scrapeJob = databaseService.getScrapeJobById(idNum);
    if (scrapeJob.isEmpty()) {
      return Optional.of(ScrapeJobMessage.JOB_NOT_FOUND);
    }

    // get the scraping executor type
    ScrapingExecutorType executorType = ScrapingExecutorType.GetTypeFromScrapeJob(scrapeJob.get());
    if (executorType == null) {
      return Optional.of(ScrapeJobMessage.JOB_SITE_NOT_VALID);
    }

    // create the executor
    ScrapingExecutor executor = executorType.getInstance();
    if (executor == null) {
      return Optional.of(GenericMessage.EXECUTOR_NOT_AVAILABLE);
    }
    executor.setScrapeJob(scrapeJob.get());
    executor.setDatabaseService(databaseService);

    // check if we are already executing this job
    boolean alreadyExecuting = false;
    ImmutableList<ScrapingExecutorType> types = ImmutableList.copyOf(this.jobsInProgress);
    for (ScrapingExecutorType type : types) {
      if (executorType.equals(type)) {
        alreadyExecuting = true;
        break;
      }
    }
    if (alreadyExecuting) {
      return Optional.of(ScrapeJobMessage.ALREADY_EXECUTING);
    }

    jobsInProgress.add(executorType);

    ListenableFuture<ScrapingExecutor> scrapingExecutorFuture =
        executorService.submit(
            () -> {
              executor.scrape();
              return executor;
            });

    Futures.addCallback(
        scrapingExecutorFuture,
        new FutureCallback<>() {
          @Override
          public void onSuccess(ScrapingExecutor result) {
            jobsInProgress.remove(executorType);
          }

          @Override
          public void onFailure(Throwable t) {
            jobsInProgress.remove(executorType);
          }
        },
        executorService);

    return Optional.empty();
  }

  public ScrapeJob createScrapeJob(ScrapeJob scrapeJob) {
    checkNotNull(scrapeJob);
    scrapeJob = databaseService.storeScrapeJobInDatabase(scrapeJob);
    return scrapeJob;
  }

  public List<ScrapeJob> getAllScrapeJobs() {
    return databaseService.getAllScrapeJobs();
  }
}
