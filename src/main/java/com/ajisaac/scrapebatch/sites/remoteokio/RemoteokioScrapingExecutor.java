package com.ajisaac.scrapebatch.sites.remoteokio;

import com.ajisaac.scrapebatch.dto.JobPosting;
import com.ajisaac.scrapebatch.dto.ScrapeJob;
import com.ajisaac.scrapebatch.scrape.ScrapingExecutorType;
import com.ajisaac.scrapebatch.scrape.SinglePageScrapingExecutor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RemoteokioScrapingExecutor extends SinglePageScrapingExecutor {
  private final String remoteokioUrl = "https://remoteok.io/remote-dev-jobs";

  @Override
  protected List<JobPosting> parseMainPage(String mainPage) {
    // parse the page for jobs
    Document document = Jsoup.parse(mainPage);
    Element jobsTable = document.selectFirst("table#jobsboard");

    Elements tableRows = jobsTable.getElementsByTag("tr");

    List<Element> jobs =
        tableRows.stream()
            .filter(
                tableRow -> {
                  String dataUrl = tableRow.attr("data-url");
                  if (dataUrl.isBlank()) {
                    return false;
                  }
                  return dataUrl.contains("/remote-jobs/");
                })
            .collect(Collectors.toList());

    List<JobPosting> jobPostings = new ArrayList<>();

    for (Element job : jobs) {
      String dataId = job.attr("data-id");
      if (dataId.isBlank()) {
        continue;
      }
      // trying to find it's matching tr data
      Elements matches = jobsTable.select("tr[data-id=" + dataId + "]");
      // narrow it down
      Optional<Element> match =
          matches.stream().filter(tr -> tr.attr("data-url").isBlank()).findFirst();
      Element m = null;
      if (match.isPresent()) {
        m = match.get();
      }
      JobPosting jobPosting = parseBasicJobPosting(job, m);
      if (jobPosting != null) {
        jobPostings.add(jobPosting);
      }
    }
    return jobPostings;
  }

  /** Parse the basic job that we got from the main page. */
  private JobPosting parseBasicJobPosting(Element job, Element additionalDetails) {
    JobPosting jobPosting = new JobPosting();

    // company;
    String company = job.attr("data-company");
    jobPosting.setCompany(company);

    // jobTitle;

    Element t = job.selectFirst("[itemprop=title]");
    String jobTitle = t != null ? t.text() : "";
    jobPosting.setJobTitle(jobTitle);

    // href;
    Element u = job.selectFirst("[itemprop=url]");
    if (u != null) {
      String url = u.attr("href");
      jobPosting.setHref("https://remoteok.io" + url);
    }

    // location;
    Element l = job.selectFirst(".location");
    String location = l != null ? l.text() : "";
    jobPosting.setLocation(location);

    // date;
    Element te = job.selectFirst("time[datetime]");
    if(te != null){
      String date = te.attr("datetime");
      jobPosting.setDate(date);
    }

    // tags;
    Elements tagElements = job.select("td.tags div.tag > h3");
    if (tagElements.isEmpty()) {
      jobPosting.setTags("");
    } else {
      List<String> tags = tagElements.eachText();
      String tagString = String.join(" - ", tags);
      jobPosting.setTags(tagString);
    }

    // description;
    boolean containsDetailedDescription = false;
    if (additionalDetails != null) {
      Element descriptionDiv =
          additionalDetails.selectFirst("div[itemprop=description]>div.markdown");
      if (descriptionDiv != null) {
        containsDetailedDescription = true;
        String description = descriptionDiv.text();
        jobPosting.setDescription(description);
      }
    }

    // if not, grab the detailed description
    if (!containsDetailedDescription) {}

    // misc
    if (additionalDetails != null) {
      Element stats = additionalDetails.selectFirst("div.expandContents > span");
      if (stats != null) {
        String s = stats.text();
        if (s.contains("applied") || s.contains("viewed")) {
          jobPosting.setMiscText(s);
        }
      }
    }

    jobPosting.setStatus("new");
    jobPosting.setIgnoreScrapeDescriptionPage(true);

    return jobPosting;
  }

  @Override
  protected JobPosting parseJobDescriptionPage(String jobDescriptionPage, JobPosting jobPosting) {
    return jobPosting;
  }

  @Override
  public JobPosting setJobSite(JobPosting jobPosting) {
    jobPosting.setJobSite(ScrapingExecutorType.REMOTEOKIO.toString());
    return jobPosting;
  }

  @Override
  public void setScrapeJob(ScrapeJob scrapeJob) {
    // no need currently
  }

  @Override
  protected String getMainPageHref() {
    return remoteokioUrl;
  }
}