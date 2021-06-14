package com.ajisaac.scrapebatch.service;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class CompanyService {
  public List<String> getBlacklisted() {
    return new ArrayList<>();
  }

  public List<String> getGraylisted() {
    return new ArrayList<>();
  }
}
