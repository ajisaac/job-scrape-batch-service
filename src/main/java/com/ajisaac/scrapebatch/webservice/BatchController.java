package com.ajisaac.scrapebatch.webservice;

import com.ajisaac.scrapebatch.BatchJobService;
import com.ajisaac.scrapebatch.OutputError;
import com.ajisaac.scrapebatch.indeed.IndeedSearch;
import com.ajisaac.scrapebatch.indeed.IndeedSearchValidationStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/batch")
public class BatchController {

  private final BatchJobService batchJobService;

  @Autowired
  public BatchController(BatchJobService batchJobService) {
    this.batchJobService = batchJobService;
  }

  /**
   * Creates a job in our system as long as the job is correct.
   *
   * @param job a IndeedSearch job from our client.
   * @return Error or the accepted Job.
   */
  @PostMapping("/indeed/job")
  public ResponseEntity submitJob(@RequestBody IndeedSearch job) {

    IndeedSearchValidationStatus validationStatus = IndeedSearchValidationStatus.ValidateJob(job);
    if (validationStatus != IndeedSearchValidationStatus.VALID) {
      OutputError outputError =
          new OutputError(validationStatus.toString(), validationStatus.getErrorMessage());
      return new ResponseEntity(outputError, HttpStatus.BAD_REQUEST);
    }

    job = batchJobService.submitIndeedScrapeJob(job);
    return new ResponseEntity(job, HttpStatus.CREATED);

  }
}
