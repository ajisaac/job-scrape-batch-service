package com.ajisaac.scrapebatch.scrape;

import com.ajisaac.scrapebatch.dto.DatabaseService;
import com.ajisaac.scrapebatch.dto.JobPosting;
import com.ajisaac.scrapebatch.network.PageGrabber;
import com.ajisaac.scrapebatch.network.WebsocketNotifier;
import com.ajisaac.scrapebatch.scraper.SinglePageScraper;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * This type of class will have the ability to scrape a static non javascript site where all the
 * results are contained in a single page.
 */
public class SinglePageScrapingExecutor implements ScrapingExecutor {

  private final SinglePageScraper scraper;
  private final String name;
  private DatabaseService databaseService;
  private WebsocketNotifier notifier;

  /** initialize this with a scraper */
  public SinglePageScrapingExecutor(SinglePageScraper scraper) {
    this.scraper = scraper;
    this.name = scraper.getJobSite().name();
  }

  /** a way to pass messages out to the frontend */
  @Override
  public void setWebsocketNotifier(WebsocketNotifier notifier){
    this.notifier = notifier;
  }

  /** the database to put the jobs in */
  @Override
  public void setDatabaseService(DatabaseService databaseService) {
    this.databaseService = databaseService;
  }

  /** Scrapes a job site once. */
  @Override
  public void scrape() {

    // get the page to scrape
    final String href = scraper.getMainPageHref();

    // make the request for that url
    notifier.scrapingMainPage(href, this.name);
    String mainPage = PageGrabber.grabPage(href);
    if (mainPage == null) {
      notifier.failMainPageScrape(href, this.name);
      return;
    }

    notifier.successfulMainPageScrape(href, this.name);
    List<JobPosting> jobPostings = scraper.parseMainPage(mainPage);
    notifier.foundPostings(jobPostings.size(), this.name, href);

    for (JobPosting jobPosting : jobPostings) {
      if (!jobPosting.isIgnoreScrapeDescriptionPage()) {
        pause(10);
        notifier.scrapingDescPage(jobPosting.getHref(), this.name);
        String jobDescriptionPage = PageGrabber.grabPage(jobPosting.getHref());
        if (jobDescriptionPage == null || jobDescriptionPage.isBlank()) {
          notifier.failedDescPageScrape(jobPosting.getHref(), this.name);
          continue;
        }
        jobPosting = scraper.parseJobDescriptionPage(jobDescriptionPage);
      }
      if (jobPosting != null) {
        notifier.successfulDescPageScrape(jobPosting, this.name);
        jobPosting.setJobSite(name);
        jobPosting.setStatus("new");
        databaseService.storeJobPostingInDatabase(jobPosting);
      }
    }
  }

  /** pauses the scraping to emulate being human */
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
