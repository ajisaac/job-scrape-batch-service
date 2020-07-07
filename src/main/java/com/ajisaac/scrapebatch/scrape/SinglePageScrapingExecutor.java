package com.ajisaac.scrapebatch.scrape;

import com.ajisaac.scrapebatch.dto.JobPosting;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * This type of class will have the ability to scrape a static non javascript site where all the
 * results are contained in a single page.
 */
public abstract class SinglePageScrapingExecutor extends ScrapingExecutor {

  /** Scrapes a jobsite once. */
  @Override
  public void scrape() {

    // get the page to scrape
    URI uri = getMainPageURI();
    if (uri == null) return;

    // make the request for that url
    String mainPage = grabPage(uri);

    List<JobPosting> jobPostings = parseMainPage(mainPage);

    for (JobPosting jobPosting : jobPostings) {
      pause(pauseTime);
      try {
        String jobDescriptionPage = grabPage(jobPosting.getHref());
        if(jobDescriptionPage == null || jobDescriptionPage.isBlank()){
          continue;
        }
        jobPosting = parseJobDescriptionPage(jobDescriptionPage, jobPosting);
      } catch (URISyntaxException e) {
        // just don't store the descriptions and such if there is an error
        e.printStackTrace();
      }
      if(jobPosting != null){
        super.storeInDatabase(jobPosting);
      }
    }
  }

  /**
   * If we are using a paginated site, we will need to call this to figure out what the next page to
   * run is.
   *
   * @return The next URI to scrape.
   */
  protected abstract URI getMainPageURI();
}
