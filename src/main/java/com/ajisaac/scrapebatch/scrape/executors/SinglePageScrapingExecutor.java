package com.ajisaac.scrapebatch.scrape.executors;

import com.ajisaac.scrapebatch.dto.DatabaseService;
import com.ajisaac.scrapebatch.dto.JobPosting;
import com.ajisaac.scrapebatch.network.PageGrabber;
import com.ajisaac.scrapebatch.network.WebsocketNotifier;
import com.ajisaac.scrapebatch.scrape.CleanseDescription;
import com.ajisaac.scrapebatch.scrape.scrapers.Scraper;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * This type of class will have the ability to scrape a static non javascript site where all the
 * results are contained in a single page.
 */
public class SinglePageScrapingExecutor implements ScrapingExecutor {

  private final Scraper scraper;
  private final String name;
  private DatabaseService databaseService;
  private WebsocketNotifier notifier;

  public SinglePageScrapingExecutor(Scraper scraper) {
    this.scraper = scraper;
    this.name = scraper.getJobSite().name();
  }

  @Override
  public void setWebsocketNotifier(WebsocketNotifier notifier) {
    this.notifier = notifier;
  }

  @Override
  public void setDb(DatabaseService db) {
    this.databaseService = db;
  }

  @Override
  public void scrape() {
    final var href = scraper.getNextMainPageURI();
    String mainPage = PageGrabber.grabPage(href);
    if (mainPage == null) {
      notifier.failMainPageScrape(href.toString(), this.name);
      return;
    }
    notifier.successfulMainPageScrape(href.toString(), this.name);
    List<JobPosting> jobPostings = scraper.parseMainPage(mainPage);
    notifier.foundPostings(jobPostings.size(), this.name, href.toString());
    for (JobPosting jobPosting : jobPostings) {
      if (jobPosting == null) continue;
      if (!jobPosting.isIgnoreScrapeDescriptionPage()) {
        pause(10);
        notifier.scrapingDescPage(jobPosting.getHref(), this.name);
        String jobDescriptionPage = PageGrabber.grabPage(jobPosting.getHref());
        if (jobDescriptionPage == null || jobDescriptionPage.isBlank()) {
          notifier.failedDescPageScrape(jobPosting.getHref(), this.name);
          continue;
        }
        scraper.parseJobDescriptionPage(jobDescriptionPage, jobPosting);
        notifier.successfulDescPageScrape(jobPosting, this.name);
      }
      cleanseDescription(jobPosting);
      jobPosting.setJobSite(this.name);
      jobPosting.setStatus("new");
      databaseService.storeJobPostingInDatabase(jobPosting);
    }
  }

  @Override
  public void stopScraping() {
    // currently not implemented
  }

  private void cleanseDescription(JobPosting jobPosting) {
    String desc = jobPosting.getDescription();
    if (desc != null) {
      desc = CleanseDescription.cleanse(desc);
      jobPosting.setDescription(desc);
    }
  }

  private void pause(int maxSeconds) {
    try {
      int r = ThreadLocalRandom.current().nextInt(maxSeconds) + 1;
      notifier.sleeping(r, this.name);
      TimeUnit.SECONDS.sleep(r);
    } catch (InterruptedException e) {
      notifier.error(e, this.name);
      e.printStackTrace();

    }
  }
}
