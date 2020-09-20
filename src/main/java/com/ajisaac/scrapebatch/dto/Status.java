package com.ajisaac.scrapebatch.dto;

import com.google.common.base.Strings;

public enum Status {
  NEW,
  SAVED,
  APPLIED,
  INTERVIEWING,
  EXCLUDED,
  REJECTED,
  IGNORED;

  /**
   * Gets the status as a lowercase String.
   *
   * @return The status as a String.
   */
  public String getLowercase() {
    return this.toString().toLowerCase();
  }

  /**
   * Attempts to find the status by name.
   *
   * @param name The name of the status.
   * @return The Status or null.
   */
  public static Status getStatusByName(String name) {
    if (Strings.nullToEmpty(name).isBlank()) {
      return null;
    }
    try {
      return Status.valueOf(name.trim().toUpperCase());
    } catch (IllegalArgumentException ex) {
      return null;
    }
  }
}
