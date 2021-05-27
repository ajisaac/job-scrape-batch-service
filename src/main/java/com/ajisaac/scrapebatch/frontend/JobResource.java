package com.ajisaac.scrapebatch.frontend;

import com.ajisaac.scrapebatch.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.util.List;

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
  public ResponseEntity updateJobStatus(
    @PathVariable("id")
    @Min(1)
    @Positive
    @NotBlank
    @Digits(message = "Id must be integral.", integer = 10, fraction = 10)
      Long id,
    @PathVariable("status") @NotBlank String status) {
    JobPosting jobPosting = jobService.updateJobStatus(id, status);
    if (jobPosting == null) {
      return new ResponseEntity<>("Unable to update status.", HttpStatus.BAD_REQUEST);
    }
    return new ResponseEntity<>(jobPosting, HttpStatus.OK);
  }

  @PutMapping("/status/multiple/{status}")
  public ResponseEntity updateMultipleJobStatuses(
    @RequestBody List<Long> jobStatuses, @PathVariable("status") @NotBlank String status) {
    List<JobPosting> jobPostings = jobService.updateMultipleJobStatuses(jobStatuses, status);
    return new ResponseEntity<>(jobPostings, HttpStatus.OK);
  }


  @GetMapping("/blacklistedcompanies")
  public ResponseEntity<List<String>> getBlacklistedCompanies() {
    List<String> blacklistedCompanies = jobService.getBlacklistedCompanies();
    return new ResponseEntity<>(blacklistedCompanies, HttpStatus.OK);
  }

  @PostMapping("/blacklistcompany")
  public ResponseEntity<List<String>> addBlacklistedCompany(@RequestBody BlacklistedCompany blc) {
    jobService.addBlacklistedCompany(blc);
    List<String> blacklistedCompanies = jobService.getBlacklistedCompanies();
    return new ResponseEntity<>(blacklistedCompanies, HttpStatus.OK);
  }

  @PostMapping("/blacklistcompanyremove")
  public ResponseEntity<List<String>> deleteBlacklistedCompany(@RequestBody BlacklistedCompany blc) {
    jobService.deleteBlacklistedCompany(blc);
    List<String> blacklistedCompanies = jobService.getBlacklistedCompanies();
    return new ResponseEntity<>(blacklistedCompanies, HttpStatus.OK);
  }

}
