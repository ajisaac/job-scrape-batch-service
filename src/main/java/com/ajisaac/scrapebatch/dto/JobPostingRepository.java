package com.ajisaac.scrapebatch.dto;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobPostingRepository extends JpaRepository<JobPosting, Long>{
  List<JobPosting> findAllByHref(String href);
  List<JobPosting> findAllByJobSite(String jobSite);
}
