package com.ajisaac.scrapebatch.indeed;

import com.google.common.base.Strings;

/** Use these statuses for informing the user that the job is invalid. */
public enum IndeedSearchValidationStatus {
  NAME_IS_REQUIRED("name for job is required"),
  QUERY_IS_REQUIRED("query is required"),
  LOCATION_IS_REQUIRED("location is required"),
  JOB_TYPE_NOT_VALID("jobType is not valid"),
  SORT_TYPE_NOT_VALID("sortType is not valid"),
  VALID("Job is valid.");

  private final String errorMessage;

  IndeedSearchValidationStatus(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  /**
   * Validates that the job data is correct, returning the validity status.
   *
   * @param job The job to validate.
   * @return The status of the job's validity.
   */
  public static IndeedSearchValidationStatus ValidateJob(IndeedSearch job) {
    if (Strings.nullToEmpty(job.getName()).isBlank()) {
      return NAME_IS_REQUIRED;
    }

    if (Strings.nullToEmpty(job.getQuery()).isBlank()) {
      return QUERY_IS_REQUIRED;
    }

    if (Strings.nullToEmpty(job.getLocation()).isBlank()) {
      return LOCATION_IS_REQUIRED;
    }

    // job type is optional
    if (!Strings.nullToEmpty(job.getJobType()).isBlank()) {
      if (!isJobTypeValid(job.getJobType())) {
        return JOB_TYPE_NOT_VALID;
      }
    }

    // sort type is optional
    if (!Strings.nullToEmpty(job.getSortType()).isBlank()) {
      if (!isSortTypeValid(job.getJobType())) {
        return SORT_TYPE_NOT_VALID;
      }
    }

    return VALID;
  }

  /** There's a predefined set of possible sort types. */
  private static boolean isSortTypeValid(String sortType) {
    for (SortType st : SortType.values()) {
      if (sortType.equals(st.toString())) {
        return true;
      }
    }
    return false;
  }

  /** There's a predefined set of possible job types. */
  private static boolean isJobTypeValid(String jobType) {
    for (JobType jt : JobType.values()) {
      if (jobType.equals(jt.toString())) {
        return true;
      }
    }
    return false;
  }
}
