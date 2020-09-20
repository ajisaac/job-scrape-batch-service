package com.ajisaac.scrapebatch.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Companies {
  private final List<Company> companies;

  public Companies() {
    this.companies = new ArrayList<>();
  }

  public void addCompany(Company company) {
    if (company == null) return;
    this.companies.add(company);
  }

  public List<Company> getCompanies() {
    return companies;
  }
}
