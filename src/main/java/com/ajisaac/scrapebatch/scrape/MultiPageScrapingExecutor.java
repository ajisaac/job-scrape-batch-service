package com.ajisaac.scrapebatch.scrape;

import com.ajisaac.scrapebatch.dto.JobPosting;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * This type of class will have the ability to scrape a static non javascript site where all the
 * results are contained across multiple pages.
 */
public abstract class MultiPageScrapingExecutor extends ScrapingExecutor {

  protected boolean hasMorePages = true;

  /** Scrapes a job site once. */
  @Override
  public void scrape() {

    while (hasMorePages) {
      pause(pauseTime);
      // get the page to scrape
      URI uri = getNextMainPageURI();
      if(uri == null){
        // todo handle this
        break;
      }

      // make the request for that url
      String mainPage = grabPage(uri);
      if(mainPage == null){
        // todo handle this
        break;
      }

      List<JobPosting> jobPostings = parseMainPage(mainPage);

      for (JobPosting jobPosting : jobPostings) {
        pause(pauseTime);
        try {
          String jobDescriptionPage = grabPage(jobPosting.getHref());
          if (jobDescriptionPage == null) {
            continue;
          }
          jobPosting = parseJobDescriptionPage(jobDescriptionPage, jobPosting);
        } catch (URISyntaxException e) {
          e.printStackTrace();
          // we simply won't store this job posting with any of it's details.
        }
        if (jobPosting != null) {
          jobPosting = setJobSite(jobPosting);
          super.storeInDatabase(jobPosting);
        }
      }
    }
  }

  /**
   * If we are using a paginated site, we will need to call this to figure out what the next page to
   * run is.
   *
   * @return The next URI to scrape.
   */
  protected abstract URI getNextMainPageURI();

  /**
   * Here we can update if we should keep scraping more pages.
   *
   * @param shouldKeepScraping Should we keep scraping more pages.
   */
  protected void updateShouldKeepScraping(boolean shouldKeepScraping) {
    this.hasMorePages = shouldKeepScraping;
  }
}
