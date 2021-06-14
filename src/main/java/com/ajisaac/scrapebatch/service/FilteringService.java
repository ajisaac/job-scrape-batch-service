package com.ajisaac.scrapebatch.service;

import com.ajisaac.scrapebatch.dto.Filtering;
import com.ajisaac.scrapebatch.dto.JobPosting;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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

  private List<JobPosting> filterByStatuses(List<String> statuses, List<JobPosting> postings) {
    List<String> newStatuses = new ArrayList<>();
    for (var s : statuses) {
      newStatuses.add(s.toLowerCase(Locale.ROOT));
    }
    return postings
      .stream()
      .filter(jobPosting -> {
        // include postings with one of these statuses
        var s = jobPosting.getStatus();
        if (s == null) {
          return false;
        }
        for (var status : newStatuses) {
          if (s.equals(status)) {
            return true;
          }
        }
        return false;
      })
      .collect(Collectors.toList());
  }

  // both searches everything and applies our paging limit
  private List<JobPosting> filterSearchTexts(List<String> descriptionTexts,
                                             List<String> titleTexts,
                                             List<JobPosting> postings,
                                             int limit) {

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
    if (limit == 0) {
      limit = 10;
    }

    List<JobPosting> newPostings = new ArrayList<>();

    for (var posting : postings) {
      if (found >= limit) {
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

  private List<JobPosting> filterByJobSite(List<String> jobSites, List<JobPosting> postings) {
    return postings
      .stream()
      .filter(jobPosting -> {
        for (var js : jobSites) {
          if (js.equals(jobPosting.getJobSite())) {
            return true;
          }
        }
        return false;
      })
      .collect(Collectors.toList());
  }

  private List<JobPosting> filterOutCompanyList(List<String> companies, List<JobPosting> postings) {
    List<String> comps = new ArrayList<>();
    for (var c : companies) {
      comps.add(c.toLowerCase(Locale.ROOT));
    }
    return postings
      .stream()
      .filter(jobPosting -> {
        var jp = jobPosting.getCompany().toLowerCase(Locale.ROOT);
        for (var c : comps) {
          if (c.equals(jp)) {
            return false;
          }
        }
        return true;
      })
      .collect(Collectors.toList());
  }

  /**
   * optimal filtering against these parameters
   */
  public List<JobPosting> optimalFiltering(List<JobPosting> postings, Filtering filtering) {
    if (filtering == null) {
      return postings;
    }

    // widest swatch of jobs to filter
    List<String> jobSites = filtering.getJobSites();
    if (jobSites != null && !jobSites.isEmpty()) {
      postings = filterByJobSite(jobSites, postings);
    }

    // if there is company filtering
    boolean filteredCompanyName = false;
    String company = filtering.getCompany();
    if (company != null && !company.isBlank()) {
      // we don't need to black or greylist
      postings = filterByCompany(company, postings);
      filteredCompanyName = true;
    }

    List<String> statuses = filtering.getStatuses();
    if (statuses != null && !statuses.isEmpty()) {
      // there is some amount of statuses to filter by
      postings = filterByStatuses(statuses, postings);
    }

    if (!filteredCompanyName) {

      if (filtering.isFilterGraylist()) {
        List<String> greylisted = companyService.getGraylisted();
        if (greylisted != null && !greylisted.isEmpty()) {
          postings = filterOutCompanyList(greylisted, postings);
        }
      }

      if (filtering.isFilterBlacklist()) {
        List<String> blacklisted = companyService.getBlacklisted();
        if (blacklisted != null && !blacklisted.isEmpty()) {
          postings = filterOutCompanyList(blacklisted, postings);
        }
      }

    }

    // search the title/description
    List<String> descriptionTexts = filtering.getJobDescriptionTexts();
    List<String> titleTexts = filtering.getJobTitleTexts();

    postings = filterSearchTexts(descriptionTexts, titleTexts, postings, LIMIT);

    return postings;
  }


}
