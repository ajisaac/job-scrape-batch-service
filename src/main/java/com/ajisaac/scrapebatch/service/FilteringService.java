package com.ajisaac.scrapebatch.service;

import com.ajisaac.scrapebatch.dto.Filtering;
import com.ajisaac.scrapebatch.dto.JobPosting;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Run our job postings through here to filter
 */
@Singleton
public class FilteringService {

  private final static int LIMIT = 10;

  @Inject
  CompanyService companyService;

  private List<JobPosting> filterByCompany(String company, List<JobPosting> postings) {
    var c = company.toLowerCase(Locale.ROOT);
    return postings
      .stream()
      .filter(jobPosting -> jobPosting.getCompany() != null
        && c.equals(jobPosting.getCompany().toLowerCase(Locale.ROOT)))
      .collect(Collectors.toList());
  }

  private List<JobPosting> filterByStatuses(Map<String, Boolean> statuses, List<JobPosting> postings) {
    if (statuses == null || statuses.isEmpty()) {
      return postings;
    }

    boolean allChecked = true;
    for (Boolean checked : statuses.values()) {
      if (!checked) {
        allChecked = false;
        break;
      }
    }
    if (allChecked) {
      return postings;
    }

    boolean noneChecked = true;
    for (Boolean checked : statuses.values()) {
      if (checked) {
        noneChecked = false;
        break;
      }
    }
    if (noneChecked) {
      return postings;
    }


    return postings
      .stream()
      .filter(jobPosting -> {
        // include postings with one of these statuses
        var s = jobPosting.getStatus();
        if (s == null)
          return false;

        for (var status : statuses.entrySet())
          if (status.getValue() && status.getKey().equals(s))
            return true;

        return false;
      })
      .collect(Collectors.toList());
  }

  // both searches everything and applies our paging limit
  private List<JobPosting> filterSearchTexts(List<String> descriptionTexts,
                                             List<String> titleTexts,
                                             List<JobPosting> postings) {

    if (postings == null || postings.isEmpty()) {
      return new ArrayList<>();
    }

    if (descriptionTexts == null) {
      descriptionTexts = new ArrayList<>();
    }
    List<String> newDescriptionTexts = new ArrayList<>();
    for (var t : descriptionTexts) {
      newDescriptionTexts.add(t.toLowerCase(Locale.ROOT));
    }

    if (titleTexts == null) {
      titleTexts = new ArrayList<>();
    }
    List<String> newTitleTexts = new ArrayList<>();
    for (var t : titleTexts) {
      newTitleTexts.add(t.toLowerCase(Locale.ROOT));
    }

    int found = 0;

    List<JobPosting> newPostings = new ArrayList<>();

    for (var posting : postings) {
      if (found >= LIMIT) {
        break;
      }

      if (newTitleTexts.isEmpty() || searchTitleText(posting, newTitleTexts)) {
        if (newDescriptionTexts.isEmpty() || searchDescriptionText(posting, newDescriptionTexts)) {
          // if we aren't searching at all, just add everything
          newPostings.add(posting);
          found++;
        }
      }
    }
    return newPostings;
  }

  // additive filtering
  private boolean searchTitleText(JobPosting posting, List<String> newTitleTexts) {
    for (var t : newTitleTexts) {
      var title = posting.getJobTitle();
      if (title == null || title.isBlank()) {
        continue;
      }
      if (title.toLowerCase(Locale.ROOT).contains(t)) {
        return true;
      }
    }
    return false;
  }

  // additive filtering
  private boolean searchDescriptionText(JobPosting posting, List<String> newDescriptionTexts) {
    for (var t : newDescriptionTexts) {
      var description = posting.getDescription();
      if (description == null || description.isBlank()) {
        continue;
      }
      if (description.toLowerCase(Locale.ROOT).contains(t)) {
        return true;
      }
    }
    return false;
  }

  private List<JobPosting> filterByJobSite(Map<String, Boolean> jobSites, List<JobPosting> postings) {
    if (jobSites == null || jobSites.isEmpty()) {
      return postings;
    }

    boolean allChecked = true;
    for (Boolean checked : jobSites.values()) {
      if (!checked) {
        allChecked = false;
        break;
      }
    }
    if (allChecked) {
      return postings;
    }

    boolean noneChecked = true;
    for (Boolean checked : jobSites.values()) {
      if (checked) {
        noneChecked = false;
        break;
      }
    }
    if (noneChecked) {
      return postings;
    }

    return postings
      .stream()
      .filter(jobPosting -> {
        var s = jobPosting.getJobSite();
        if (s == null)
          return false;

        for (var js : jobSites.entrySet())
          if (js.getValue() && js.getKey().toLowerCase(Locale.ROOT).equals(jobPosting.getJobSite().toLowerCase(Locale.ROOT))) return true;

        return false;
      })
      .collect(Collectors.toList());
  }

  public List<JobPosting> optimalFiltering(List<JobPosting> postings, Filtering filtering) {
    if (filtering == null) {
      return postings;
    }

    // widest swatch of jobs to filter
    Map<String, Boolean> jobSites = filtering.getJobSites();
    if (jobSites != null && !jobSites.isEmpty()) {
      postings = filterByJobSite(jobSites, postings);
    }

    // if there is company filtering
    String company = filtering.getCompany();
    if (company != null && !company.isBlank()) {
      // we don't need to black or greylist
      postings = filterByCompany(company, postings);
    }

    Map<String, Boolean> statuses = filtering.getStatuses();
    if (statuses != null && !statuses.isEmpty()) {
      // there is some amount of statuses to filter by
      postings = filterByStatuses(statuses, postings);
    }


    // search the title/description
    List<String> descriptionTexts = Arrays.asList(filtering.getJobDescriptionText().split(","));
    List<String> titleTexts = Arrays.asList(filtering.getJobTitleText().split(","));

    postings = filterSearchTexts(descriptionTexts, titleTexts, postings);

    return postings;
  }


}
