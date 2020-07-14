package com.ajisaac.scrapebatch.scrape;

import com.ajisaac.scrapebatch.dto.JobPosting;
import com.google.common.base.Strings;

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
    final String href = getMainPageHref();
    URI uri = getMainPageURI(href);
    if (uri == null) return;

    // make the request for that url
    String mainPage = grabPage(uri);

    List<JobPosting> jobPostings = parseMainPage(mainPage);

    for (JobPosting jobPosting : jobPostings) {
      if (!jobPosting.isIgnoreScrapeDescriptionPage()) {
        pause(pauseTime);
        try {
          String jobDescriptionPage = grabPage(jobPosting.getHref());
          if (jobDescriptionPage == null || jobDescriptionPage.isBlank()) {
            continue;
          }
          jobPosting = parseJobDescriptionPage(jobDescriptionPage, jobPosting);
        } catch (URISyntaxException e) {
          // just don't store the descriptions and such if there is an error
          e.printStackTrace();
        }
      }
      if (jobPosting != null) {
        jobPosting = setJobSite(jobPosting);
        jobPosting.setStatus("new");
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
  protected URI getMainPageURI(String href) {
    if (Strings.nullToEmpty(href).isBlank()) {
      return null;
    }
    try {
      return new URI(href);
    } catch (URISyntaxException e) {
      return null;
    }
  }

  /**
   * For a single page scraper, this would return the href to scrape.
   *
   * @return The url of a page to scrape.
   */
  protected abstract String getMainPageHref();
}
