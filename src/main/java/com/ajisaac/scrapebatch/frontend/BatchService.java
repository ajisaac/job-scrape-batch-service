package com.ajisaac.scrapebatch.frontend;

import com.ajisaac.scrapebatch.dto.DatabaseService;
import com.ajisaac.scrapebatch.dto.ScrapeJob;
import com.ajisaac.scrapebatch.network.WebsocketNotifier;
import com.ajisaac.scrapebatch.scrape.ScrapingExecutorType;
import com.ajisaac.scrapebatch.scrape.executors.ScrapingExecutor;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.Executors;

import static com.google.common.base.Preconditions.checkNotNull;

@Service
public class BatchService {

  private final DatabaseService db;
  private final WebsocketNotifier notifier;
  private final List<ScrapingExecutorType> jobsInProgress =
    Collections.synchronizedList(new ArrayList<>());
  private final ListeningExecutorService executorService =
    MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(10));

  @Autowired
  public BatchService(DatabaseService db, WebsocketNotifier notifier) {
    this.db = db;
    this.notifier = notifier;
  }

  public boolean isCurrentlyScraping(long idNum) {
    var scrapeJob = db.getScrapeJobById(idNum);
    if (scrapeJob == null)
      return false;

    var type = scrapeJob.getTypeFromScrapeJob();
    ImmutableList<ScrapingExecutorType> jobs = ImmutableList.copyOf(this.jobsInProgress);

    for (ScrapingExecutorType j : jobs)
      if (j == type)
        return true;

    return false;
  }

  public String doScrape(long id) {

    var scrapeJob = db.getScrapeJobById(id);
    if (scrapeJob == null)
      return "Job Not Found";

    var executorType = scrapeJob.getTypeFromScrapeJob();
    if (executorType == null)
      return "Job Site Not Found";

    if (alreadyExecuting(executorType))
      return "Already Scraping this Site";

    var executor = scrapeJob.getExecutor();
    if (executor == null)
      return "Executor Not Available";

    executor.setDb(db);
    executor.setWebsocketNotifier(notifier);

    jobsInProgress.add(executorType);
    ListenableFuture<ScrapingExecutor> scrapingExecutorFuture =
      executorService.submit(
        () -> {
          executor.scrape();
          return executor;
        });

    addCallback(scrapingExecutorFuture, executorType);

    return null;
  }

  private void addCallback(ListenableFuture<ScrapingExecutor> scrapingExecutorFuture,
                           ScrapingExecutorType executorType) {

    Futures.addCallback(scrapingExecutorFuture,
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

  }

  public List<ScrapeJob> createScrapeJobs(List<ScrapeJob> scrapeJobs) {
    if (scrapeJobs == null || scrapeJobs.isEmpty())
      return new ArrayList<>();

    List<ScrapeJob> existingJobs = getAllScrapeJobs();
    List<ScrapeJob> createdJobs = new ArrayList<>();

    for (ScrapeJob sj : scrapeJobs) {
      var job = findScrapeJobIfExists(sj, existingJobs);
      if (job != null) {
        createdJobs.add(job);
      } else {
        sj = db.storeScrapeJobInDatabase(sj);
        createdJobs.add(sj);
      }
    }

    return createdJobs;
  }

  public ScrapeJob createScrapeJob(ScrapeJob scrapeJob) {
    if(scrapeJob == null)
      return null;

    List<ScrapeJob> existingJobs = getAllScrapeJobs();
    var ej = findScrapeJobIfExists(scrapeJob, existingJobs);

    if (ej != null)
      return ej;

    return db.storeScrapeJobInDatabase(scrapeJob);
  }

  private ScrapeJob findScrapeJobIfExists(ScrapeJob scrapeJob, List<ScrapeJob> existingJobs) {

    for (ScrapeJob ej : existingJobs)
      if (ej.weakEquals(scrapeJob))
        return ej;

    return null;
  }

  public List<ScrapeJob> getAllScrapeJobs() {
    return db.getAllScrapeJobs();
  }

  private boolean alreadyExecuting(ScrapingExecutorType executorType) {
    ImmutableList<ScrapingExecutorType> types = ImmutableList.copyOf(this.jobsInProgress);
    for (ScrapingExecutorType type : types)
      if (executorType.equals(type))
        return true;

    return false;
  }

}
