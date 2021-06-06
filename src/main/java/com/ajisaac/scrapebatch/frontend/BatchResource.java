package com.ajisaac.scrapebatch.frontend;

import com.ajisaac.scrapebatch.dto.ScrapeJob;
import com.ajisaac.scrapebatch.scrape.ScrapingExecutorType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/batch")
public class BatchResource {

  private final BatchService batchJobService;
  private final SimpMessagingTemplate template;

  @Autowired
  public BatchResource(BatchService batchJobService, SimpMessagingTemplate template) {
    this.batchJobService = batchJobService;
    this.template = template;
  }

  // todo fix all this weirdness, return proper responses if failure

  @PostMapping("/scrape-job")
  public ResponseEntity<ScrapeJob> createScrapeJob(@RequestBody ScrapeJob scrapeJob) {
    scrapeJob = batchJobService.createScrapeJob(scrapeJob);
    if (scrapeJob == null)
      return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

    return new ResponseEntity<>(scrapeJob, HttpStatus.ACCEPTED);
  }

  @PostMapping("/scrape-jobs")
  public ResponseEntity createScrapeJobs(@RequestBody List<ScrapeJob> scrapeJobs) {
    List<ScrapeJob> createdJobs = batchJobService.createScrapeJobs(scrapeJobs);
    return new ResponseEntity(createdJobs, HttpStatus.ACCEPTED);
  }

  @GetMapping("/scrape-jobs")
  public ResponseEntity<List<ScrapeJob>> getScrapeJobs() {
    return new ResponseEntity<>(batchJobService.getAllScrapeJobs(), HttpStatus.OK);
  }

  @GetMapping("/sites")
  public ResponseEntity<List<String>> getSites() {
    List<String> sites = new ArrayList<>();

    for (ScrapingExecutorType i : ScrapingExecutorType.values())
      sites.add(i.toString());

    return new ResponseEntity<>(sites, HttpStatus.OK);
  }

  @PostMapping("/scrape/{id}")
  public ResponseEntity<String> doScrape(@PathVariable Long id) {
    if (batchJobService.isCurrentlyScraping(id))
      return new ResponseEntity<>("Already scraping this site.", HttpStatus.OK);

    String errMsg = batchJobService.doScrape(id);
    if (errMsg == null)
      return new ResponseEntity<>("Batch scrape job " + id + " submitted", HttpStatus.OK);

    return new ResponseEntity<>(errMsg, HttpStatus.BAD_REQUEST);
  }

  @PostMapping("/message")
  public void addMessage(@RequestBody String msg){
    template.convertAndSend("/topic/messages", msg);
  }
}
