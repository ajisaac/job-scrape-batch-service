package com.ajisaac.scrapebatch.dto;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class JobPostingRepository implements PanacheRepository<JobPosting> {
  List<JobPosting> findAllByHref(String href) {
    if (href == null) {
      return new ArrayList<>();
    }
    return find("href", href).list();
  }

  List<JobPosting> findAllByJobSite(String jobSite) {
    if (jobSite == null) {
      return new ArrayList<>();
    }
    return find("jobsite", jobSite).list();
  }
}
