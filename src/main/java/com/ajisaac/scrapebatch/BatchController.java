package com.ajisaac.scrapebatch;

import com.ajisaac.scrapebatch.dto.JobPosting;
import com.ajisaac.scrapebatch.dto.Link;
import com.ajisaac.scrapebatch.dto.ScrapeJob;
import com.ajisaac.scrapebatch.scrape.ScrapingExecutorType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/batch")
public class BatchController {

  private final BatchJobService batchJobService;

  @Autowired
  public BatchController(BatchJobService batchJobService) {
    this.batchJobService = batchJobService;
  }

  /** creates one scrape job */
  @PostMapping("/scrape-job")
  public ResponseEntity submitJob(@RequestBody ScrapeJob scrapeJob) {
    scrapeJob = batchJobService.createScrapeJob(scrapeJob);
    return new ResponseEntity(scrapeJob, HttpStatus.ACCEPTED);
  }

  /** creates a bunch of jobs */
  @PostMapping("/scrape-jobs")
  public ResponseEntity submitJobs(@RequestBody List<ScrapeJob> scrapeJobs) {
    List<ScrapeJob> createdJobs = batchJobService.createScrapeJobs(scrapeJobs);
    return new ResponseEntity(createdJobs, HttpStatus.ACCEPTED);
  }

  /** gets all the scrape jobs */
  @GetMapping("/scrape-jobs")
  public ResponseEntity<List<ScrapeJob>> getScrapeJobs() {
    return new ResponseEntity<>(batchJobService.getAllScrapeJobs(), HttpStatus.OK);
  }

  /** gets all the available job sites */
  @GetMapping("/sites")
  public ResponseEntity<List<String>> getSites() {
    List<String> sites = new ArrayList<>();
    for (ScrapingExecutorType i : ScrapingExecutorType.values()) {
      sites.add(i.toString());
    }
    return new ResponseEntity<>(sites, HttpStatus.OK);
  }

  @PostMapping("/scrape/{id}")
  public ResponseEntity<String> doScrape(@PathVariable Long id) {
    if (batchJobService.isCurrentlyScraping(id)) {
      return new ResponseEntity<>("Already scraping this site.", HttpStatus.OK);
    }
    // this might return an error if the id isn't valid and so we should return that
    String message = batchJobService.doScrape(id);
    if (message == null) {
      return new ResponseEntity<>("Batch scrape job " + id + " submitted", HttpStatus.OK);
    }
    return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
  }

  /** given a link, attempt to scrape */
  @PostMapping("/scrape/link")
  public ResponseEntity<JobPosting> scrapeLink(@RequestBody Link link) {
    Optional<JobPosting> jobPosting = batchJobService.scrapeSingleJobLink(link);
    return ResponseEntity.of(jobPosting);
  }
}
