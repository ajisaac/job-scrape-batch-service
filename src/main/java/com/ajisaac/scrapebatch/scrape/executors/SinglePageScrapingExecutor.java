package com.ajisaac.scrapebatch.scrape.executors;

import com.ajisaac.scrapebatch.dto.DatabaseService;
import com.ajisaac.scrapebatch.dto.JobPosting;
import com.ajisaac.scrapebatch.network.PageGrabber;
import com.ajisaac.scrapebatch.network.WebsocketNotifier;
import com.ajisaac.scrapebatch.scrape.CleanseDescription;
import com.ajisaac.scrapebatch.scrape.ScrapingExecutor;
import com.ajisaac.scrapebatch.scrape.SinglePageScraper;

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
  private boolean stopScrapingIndicator = false;

  public SinglePageScrapingExecutor(SinglePageScraper scraper) {
    this.scraper = scraper;
    this.name = scraper.getJobSite().name();
  }

  @Override
  public void setWebsocketNotifier(WebsocketNotifier notifier) {
    this.notifier = notifier;
  }

  @Override
  public void setDatabaseService(DatabaseService databaseService) {
    this.databaseService = databaseService;
  }

  /**
   * Scrapes a job site once.
   */
  @Override
  public void scrape() {

    // get the page to scrape
    final String href = scraper.getMainPageHref();

    if (stopScrapingIndicator) {
      return;
    }
    // make the request for that url
    notifier.scrapingMainPage(href, this.name);
    if (stopScrapingIndicator) {
      return;
    }
    String mainPage = PageGrabber.grabPage(href);
    if (stopScrapingIndicator) {
      return;
    }
    if (mainPage == null) {
      notifier.failMainPageScrape(href, this.name);
      return;
    }

    if (stopScrapingIndicator) {
      return;
    }
    notifier.successfulMainPageScrape(href, this.name);

    if (stopScrapingIndicator) {
      return;
    }
    List<JobPosting> jobPostings = scraper.parseMainPage(mainPage);

    notifier.foundPostings(jobPostings.size(), this.name, href);
    if (stopScrapingIndicator) {
      return;
    }


    for (JobPosting jobPosting : jobPostings) {
      if (jobPosting == null) {
        continue;
      }
      if (stopScrapingIndicator) {
        return;
      }

      // if this is false, we've already got the good bits without needing this part
      if (!jobPosting.isIgnoreScrapeDescriptionPage()) {
        if (stopScrapingIndicator) {
          return;
        }

        pause(10);
        if (stopScrapingIndicator) {
          return;
        }

        notifier.scrapingDescPage(jobPosting.getHref(), this.name);
        if (stopScrapingIndicator) {
          return;
        }

        String jobDescriptionPage = PageGrabber.grabPage(jobPosting.getHref());
        if (stopScrapingIndicator) {
          return;
        }

        if (jobDescriptionPage == null || jobDescriptionPage.isBlank()) {
          notifier.failedDescPageScrape(jobPosting.getHref(), this.name);
          if (stopScrapingIndicator) {
            return;
          }

          continue;
        }
        scraper.parseJobDescriptionPage(jobDescriptionPage, jobPosting);
        if (stopScrapingIndicator) {
          return;
        }
        notifier.successfulDescPageScrape(jobPosting, this.name);
        if (stopScrapingIndicator) {
          return;
        }

      }
      cleanseDescription(jobPosting);

      if (stopScrapingIndicator) {
        return;
      }
      jobPosting.setJobSite(this.name);
      jobPosting.setStatus("new");
      if (stopScrapingIndicator) {
        return;
      }
      databaseService.storeJobPostingInDatabase(jobPosting);
      if (stopScrapingIndicator) {
        return;
      }
    }
  }

  @Override
  public void stopScraping() {
    this.stopScrapingIndicator = true;
  }

  /**
   * The data we get back from a job description can be wonky,
   * so we want to clean up bits of it
   */
  private void cleanseDescription(JobPosting jobPosting) {
    String desc = jobPosting.getDescription();
    if (desc != null) {
      desc = CleanseDescription.cleanse(desc);
      jobPosting.setDescription(desc);
    }
  }

  /**
   * pauses the scraping to emulate being human, avoid being a nuisance to the data
   * providers
   */
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
