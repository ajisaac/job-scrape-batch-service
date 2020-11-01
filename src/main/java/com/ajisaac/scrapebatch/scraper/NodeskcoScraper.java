package com.ajisaac.scrapebatch.scraper;

import com.ajisaac.scrapebatch.dto.JobPosting;
import com.ajisaac.scrapebatch.dto.ScrapeJob;
import com.ajisaac.scrapebatch.scrape.ScrapingExecutorType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class NodeskcoScraper implements SinglePageScraper {
  public JobPosting setJobSite(JobPosting jobPosting) {
    return null;
  }

  public void scrape() {

    //setting the driver executable
    System.setProperty("webdriver.gecko.driver", "src/main/resources/geckodriver");
    // Initiating your chromedriver
    FirefoxOptions options = new FirefoxOptions();
    options.setHeadless(true);
    WebDriver driver = new FirefoxDriver(options);

    // Applied wait time
    driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

    // open browser with desried URL
    driver.get("https://www.google.com");
    System.out.println(driver.getPageSource());

    // closing the browser
    driver.close();
    //
    //    boolean moreCurlCalls = true;
    //    int page = 0;
    //    ProcessBuilder processBuilder = new ProcessBuilder();
    //    //    while(moreCurlCalls){
    //    // while results still exist, keep making curl calls
    //    try {
    //      ArrayList<String> curl = new ArrayList<>();
    //      curl.add("curl");
    //      curl.add(
    //
    // "https://0586l1sok8-dsn.algolia.net/1/indexes/*/queries?x-algolia-agent=Algolia%20for%20vanilla%20JavaScript%203.30.0%3Binstantsearch.js%202.10.4%3BJS%20Helper%202.26.1&x-algolia-application-id=0586L1SOK8&x-algolia-api-key=29c24a146fddd62689c641f093988a38");
    //      curl.add("-H");
    //      curl.add("\"User-Agent: Mozilla/5.0 (Windows NT 10.0; rv:78.0) Gecko/20100101
    // Firefox/78.0\"");
    //      curl.add("-H");
    //      curl.add("\"Accept: application/json\"");
    //      curl.add("-H");
    //      curl.add("\"Accept-Language: en-US,en;q=0.5\"");
    //      curl.add("--compressed");
    //      curl.add("-H");
    //      curl.add("\"Referer: https://nodesk.co/remote-jobs/engineering/\"");
    //      curl.add("-H");
    //      curl.add("\"content-type: application/x-www-form-urlencoded\"");
    //      curl.add("-H");
    //      curl.add("\"Origin: https://nodesk.co\"");
    //      curl.add("-H");
    //      curl.add("\"DNT: 1\"");
    //      curl.add("-H");
    //      curl.add("\"Connection: keep-alive\"");
    //      curl.add("-H");
    //      curl.add("\"Pragma: no-cache\"");
    //      curl.add("-H");
    //      curl.add("\"Cache-Control: no-cache\"");
    //      curl.add("--data-raw");
    //
    // curl.add("'{\"requests\":[{\"indexName\":\"jobs\",\"params\":\"query=&hitsPerPage=75&page="
    //              + page
    //              +
    // "&filters=roleUrl%3Aremote-jobs%2Fengineering&facets=%5B%22regions%22%5D&tagFilters=\"}]}'");
    //      processBuilder.command(curl);
    //      Process process = processBuilder.start();
    //      InputStream is = process.getInputStream();
    //      InputStream es = process.getErrorStream();
    //      String error = CharStreams.toString(new InputStreamReader(es, Charsets.UTF_8));
    //      String result = CharStreams.toString(new InputStreamReader(is, Charsets.UTF_8));
    //
    //      ObjectMapper objectMapper = new ObjectMapper();
    //      JsonNode node = objectMapper.readTree(result);
    //      process.destroy();
    //    } catch (IOException ex) {
    //      System.out.println(ex);
    //    }

    // process the output of this call
    //    }
  }

  public void setScrapeJob(ScrapeJob scrapeJob) {}

  public JobPosting parseJobDescriptionPage(String jobDescriptionPage) {
    return null;
  }

  @Override
  public ScrapingExecutorType getJobSite() {
    return null;
  }

  public List<JobPosting> parseMainPage(String mainPage) {
    return null;
  }

  @Override
  public String getMainPageHref() {
    return null;
  }
}
