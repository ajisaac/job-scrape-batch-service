package com.ajisaac.scrapebatch.dto;

import java.util.Date;
import java.util.Objects;

public class AngelCoJobPosting {
  // do some validation on the body for sanity
  private String description;
  private String href;
  private String company;
  private String location;
  private String jobType;
  private String tags;
  private String remote;
  private String title;
  private String salary;

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getHref() {
    return href;
  }

  public void setHref(String href) {
    this.href = href;
  }

  public String getCompany() {
    return company;
  }

  public void setCompany(String company) {
    this.company = company;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public String getJobType() {
    return jobType;
  }

  public void setJobType(String jobType) {
    this.jobType = jobType;
  }

  public String getTags() {
    return tags;
  }

  public void setTags(String tags) {
    this.tags = tags;
  }

  public String getRemote() {
    return remote;
  }

  public void setRemote(String remote) {
    this.remote = remote;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getSalary() {
    return salary;
  }

  public void setSalary(String salary) {
    this.salary = salary;
  }

  @Override
  public String toString() {
    return "AngelCoJobPosting{" +
      "description='" + description + '\'' +
      ", href='" + href + '\'' +
      ", company='" + company + '\'' +
      ", location='" + location + '\'' +
      ", jobType='" + jobType + '\'' +
      ", tags='" + tags + '\'' +
      ", remote='" + remote + '\'' +
      ", title='" + title + '\'' +
      ", salary='" + salary + '\'' +
      '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AngelCoJobPosting that = (AngelCoJobPosting) o;
    return Objects.equals(description, that.description) && Objects.equals(href, that.href) && Objects.equals(company, that.company) && Objects.equals(location, that.location) && Objects.equals(jobType, that.jobType) && Objects.equals(tags, that.tags) && Objects.equals(remote, that.remote) && Objects.equals(title, that.title) && Objects.equals(salary, that.salary);
  }

  @Override
  public int hashCode() {
    return Objects.hash(description, href, company, location, jobType, tags, remote, title, salary);
  }
}

