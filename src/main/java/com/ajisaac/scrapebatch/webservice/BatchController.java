package com.ajisaac.scrapebatch.webservice;

import com.ajisaac.scrapebatch.BatchJobService;
import com.ajisaac.scrapebatch.dto.ScrapeJob;
import com.ajisaac.scrapebatch.scrape.ScrapingExecutorType;
import org.apache.logging.log4j.util.Strings;
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

  @PostMapping("/scrape-job")
  public ResponseEntity submitJob(@RequestBody ScrapeJob scrapeJob) {
    scrapeJob = batchJobService.createScrapeJob(scrapeJob);
    ScrapeJobValidation validation = new ScrapeJobValidation();
    Optional<Message> errorMessage = validation.validate(scrapeJob);

    // todo possibly return warnings as well

    if(errorMessage.isPresent()){
      return new ResponseEntity(errorMessage.get().getMessage(), HttpStatus.BAD_REQUEST);
    }
    return new ResponseEntity(scrapeJob, HttpStatus.ACCEPTED);
  }

  @GetMapping("/scrape-jobs")
  public ResponseEntity<List<ScrapeJob>> getScrapeJobs(){
    return new ResponseEntity<>(batchJobService.getAllScrapeJobs(), HttpStatus.OK);
  }

  @GetMapping("/sites")
  public ResponseEntity<List<String>> getSites() {
    List<String> sites = new ArrayList<>();
    for (ScrapingExecutorType i : ScrapingExecutorType.values()) {
      sites.add(i.toString());
    }
    return new ResponseEntity<>(sites, HttpStatus.OK);
  }

  @PostMapping("/scrape/{id}")
  public ResponseEntity<String> doScrape(@PathVariable String id) {
    // if path variable is non numeric return error
    if (Strings.isBlank(id)) {
      return new ResponseEntity<>("Id must not be null.", HttpStatus.BAD_REQUEST);
    }

    // if path variable is non numeric return error
    long idNum;
    try {
      idNum = Long.parseLong(id, 10);
    } catch (NumberFormatException ex) {
      return new ResponseEntity<>("Id not valid integer.", HttpStatus.BAD_REQUEST);
    }

    if (idNum == 0) {
      return new ResponseEntity<>("Id 0 not valid id.", HttpStatus.BAD_REQUEST);
    }

    // if we're already scraping this one then alert us to this.
    if (batchJobService.isCurrentlyScraping(idNum)) {
      return new ResponseEntity<>("Already scraping this site.", HttpStatus.OK);
    }

    // we're good to scrape
    // this might return an error if the id isn't valid and so we should return that
    Optional<Message> message = batchJobService.doScrape(idNum);
    if(message.isEmpty()){
      return new ResponseEntity<>("Batch scrape job " + idNum + " submitted", HttpStatus.OK);
    }
    return new ResponseEntity<>(message.get().getMessage(), HttpStatus.BAD_REQUEST);
  }
}