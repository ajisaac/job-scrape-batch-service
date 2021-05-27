package com.ajisaac.scrapebatch.frontend;

import com.ajisaac.scrapebatch.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// todo add validations
@RestController
@RequestMapping("/jobs")
public class JobResource {
  private final JobService jobService;

  @Autowired
  public JobResource(JobService jobService) {
    this.jobService = jobService;
  }

  @GetMapping("/all")
  public ResponseEntity<List<JobPosting>> getAllJobs() {
    List<JobPosting> jobs = jobService.getAllJobs();
    return new ResponseEntity<>(jobs, HttpStatus.OK);
  }

  @GetMapping("/all/bycompany")
  public ResponseEntity<Companies> getAllJobsByCompany() {
    var companies = jobService.getAllJobsByCompany();
    return new ResponseEntity<>(companies, HttpStatus.OK);
  }

  @GetMapping("/backup")
  public ResponseEntity<Companies> backup() {
    return getAllJobsByCompany();
  }

  @PostMapping("/new/angelco")
  public ResponseEntity<Companies> addAngelCoJobPosting(@RequestBody JobPosting posting) {
    jobService.addAngelCoJobPosting(posting);
    return getAllJobsByCompany();
  }

  @PutMapping("/status/{id}/{status}")
  public ResponseEntity updateJobStatus(@PathVariable("id") Long id,
                                        @PathVariable("status") String status) {
    var jobPosting = jobService.updateJobStatus(id, status);
    if (jobPosting == null)
      return new ResponseEntity<>("Unable to update status.", HttpStatus.BAD_REQUEST);

    return new ResponseEntity<>(jobPosting, HttpStatus.OK);
  }

  @PutMapping("/status/multiple/{status}")
  public ResponseEntity updateMultipleJobStatuses(@RequestBody List<Long> jobStatuses,
                                                  @PathVariable("status") String status) {
    var jobPostings = jobService.updateMultipleJobStatuses(jobStatuses, status);
    return new ResponseEntity<>(jobPostings, HttpStatus.OK);
  }
}
