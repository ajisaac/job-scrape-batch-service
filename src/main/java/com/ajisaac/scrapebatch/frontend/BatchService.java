package com.ajisaac.scrapebatch.frontend;

import com.ajisaac.scrapebatch.dto.DatabaseService;
import com.ajisaac.scrapebatch.dto.ScrapeJob;
import com.ajisaac.scrapebatch.network.WebsocketNotifier;
import com.ajisaac.scrapebatch.scrape.ScrapingExecutorType;
import com.ajisaac.scrapebatch.scrape.executors.ScrapingExecutor;
import org.eclipse.microprofile.context.ManagedExecutor;

import javax.inject.Singleton;
import java.util.*;

@Singleton
public class BatchService {

  private final DatabaseService db;
  private final WebsocketNotifier notifier;

  private final Map<ScrapingExecutorType, ScrapingExecutor> jobsInProgress =
    Collections.synchronizedMap(new HashMap<>());

  private final ManagedExecutor executorService = ManagedExecutor.builder().build();

  public BatchService(DatabaseService db, WebsocketNotifier notifier) {
    this.db = db;
    this.notifier = notifier;
  }

  public boolean isCurrentlyScraping(long idNum) {
    var scrapeJob = db.getScrapeJobById(idNum);
    if (scrapeJob == null)
      return false;

    var type = scrapeJob.getTypeFromScrapeJob();

    return this.jobsInProgress.get(type) != null;
  }

  public String stopScraping(long id) {
    var scrapeJob = db.getScrapeJobById(id);
    if (scrapeJob == null)
      return "Job Not Found";

    var type = scrapeJob.getTypeFromScrapeJob();
    if (type == null)
      return "Job Site Not Found";

    if (this.jobsInProgress.containsKey(type)) {
      jobsInProgress.get(type).stopScraping();
      jobsInProgress.remove(type);
      return "Stopped scraping";
    }
    return "Scrape job wasn't in progress";

  }

  public String doScrape(long id) {

    var scrapeJob = db.getScrapeJobById(id);
    if (scrapeJob == null)
      return "Job Not Found";

    var executorType = scrapeJob.getTypeFromScrapeJob();
    if (executorType == null)
      return "Job Site Not Found";

    if (this.jobsInProgress.containsKey(executorType))
      return "Already Scraping this Site";

    var executor = scrapeJob.getExecutor();
    if (executor == null)
      return "Executor Not Available";

    executor.setDb(db);
    executor.setWebsocketNotifier(notifier);

    jobsInProgress.put(executorType, executor);


    // todo add a hook into the thread that lets us stop its execution
    executorService.submit(() -> {
      executor.scrape();
      jobsInProgress.remove(executorType);
    });


    return null;
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
    if (scrapeJob == null)
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

}
