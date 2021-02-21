package com.ajisaac.scrapebatch.frontend.scraper;

import com.ajisaac.scrapebatch.dto.DatabaseService;
import com.ajisaac.scrapebatch.dto.JobPosting;
import com.ajisaac.scrapebatch.dto.Link;
import com.ajisaac.scrapebatch.dto.ScrapeJob;
import com.ajisaac.scrapebatch.network.WebsocketNotifier;
import com.ajisaac.scrapebatch.scrape.ScrapingExecutorType;
import com.ajisaac.scrapebatch.scrape.ScrapingExecutor;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.Executors;

import static com.google.common.base.Preconditions.checkNotNull;

/** Business logic for handling batch jobs, this is our singleton manager class */
@Service
public class BatchJobService {

  // takes our job and stores it in the data store
  DatabaseService databaseService;

  WebsocketNotifier notifier;

  // this is how we will keep track of currently scraped jobs
  private List<ScrapingExecutorType> jobsInProgress =
      Collections.synchronizedList(new ArrayList<>());

  private final ListeningExecutorService executorService =
      MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(10));

  @Autowired
  public BatchJobService(DatabaseService databaseService, WebsocketNotifier notifier) {
    this.databaseService = databaseService;
    this.notifier = notifier;
  }

  /**
   * Are we currently scraping this job site.
   *
   * @param idNum The id of a particular batch scrape job.
   * @return true if we are scraping, else false.
   */
  public boolean isCurrentlyScraping(long idNum) {
    Optional<ScrapeJob> s = databaseService.getScrapeJobById(idNum);
    if (s.isEmpty()) {
      return false;
    }
    ScrapingExecutorType type = ScrapingExecutorType.getTypeFromScrapeJob(s.get());
    ImmutableList<ScrapingExecutorType> jobs = ImmutableList.copyOf(this.jobsInProgress);

    for (ScrapingExecutorType j : jobs) {
      if (j == type) {
        return true;
      }
    }
    return false;
  }

  /**
   * Tells us to start scraping this job site.
   *
   * @param idNum The id of the batch job.
   */
  public String doScrape(long idNum) {

    // we're given this isNum, which relates to a particular batch job
    Optional<ScrapeJob> scrapeJob = databaseService.getScrapeJobById(idNum);
    if (scrapeJob.isEmpty()) {
      return "Job Not Found";
    }

    // get the scraping executor type
    ScrapingExecutorType executorType = ScrapingExecutorType.getTypeFromScrapeJob(scrapeJob.get());
    if (executorType == null) {
      return "Job Site Not Found";
    }

    // check if we are already executing this job
    ImmutableList<ScrapingExecutorType> types = ImmutableList.copyOf(this.jobsInProgress);
    for (ScrapingExecutorType type : types) {
      if (executorType.equals(type)) {
        return "Already Scraping this Site";
      }
    }

    // get us an executor for this job site
    ScrapingExecutor executor = ScrapingExecutorType.getInstance(scrapeJob.get());
    if (executor == null) {
      return "Executor Not Available";
    }
    executor.setDatabaseService(databaseService);
    executor.setWebsocketNotifier(notifier);

    // execute
    jobsInProgress.add(executorType);
    ListenableFuture<ScrapingExecutor> scrapingExecutorFuture =
        executorService.submit(
            () -> {
              executor.scrape();
              return executor;
            });

    // when the job is done we can let other people be made aware
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

    // this was a success
    return null;
  }

  /** create a bunch of scrapeJobs */
  public List<ScrapeJob> createScrapeJobs(List<ScrapeJob> scrapeJobs) {
    if (scrapeJobs == null || scrapeJobs.isEmpty()) {
      return new ArrayList<>();
    }

    List<ScrapeJob> existingJobs = getAllScrapeJobs();
    List<ScrapeJob> createdJobs = new ArrayList<>();
    for (ScrapeJob sj : scrapeJobs) {
      Optional<ScrapeJob> esj = findScrapeJobIfExists(sj, existingJobs);
      if (esj.isPresent()) {
        createdJobs.add(esj.get());
      } else {
        sj = databaseService.storeScrapeJobInDatabase(sj);
        createdJobs.add(sj);
      }
    }
    return createdJobs;
  }

  /** create a single scrapeJob */
  public ScrapeJob createScrapeJob(ScrapeJob scrapeJob) {
    checkNotNull(scrapeJob);

    List<ScrapeJob> existingJobs = getAllScrapeJobs();
    Optional<ScrapeJob> ej = findScrapeJobIfExists(scrapeJob, existingJobs);
    if (ej.isPresent()) {
      return ej.get();
    }

    scrapeJob = databaseService.storeScrapeJobInDatabase(scrapeJob);
    return scrapeJob;
  }

  /** if the scrapeJob exists in existingJobs, return the version from existingJobs */
  private Optional<ScrapeJob> findScrapeJobIfExists(
      ScrapeJob scrapeJob, List<ScrapeJob> existingJobs) {

    for (ScrapeJob ej : existingJobs) {
      if (ej.weakEquals(scrapeJob)) {
        return Optional.of(ej);
      }
    }
    return Optional.empty();
  }

  public List<ScrapeJob> getAllScrapeJobs() {
    return databaseService.getAllScrapeJobs();
  }

  /** given a link, try to scrape that job posting */
  public Optional<JobPosting> scrapeSingleJobLink(Link link) {
    // determine job site type
    ScrapingExecutorType type = ScrapingExecutorType.determineSiteFromUrl(link);
    if (type == null) {
      return Optional.empty();
    }

    // submit link to be scraped

    return Optional.empty();
  }
}
