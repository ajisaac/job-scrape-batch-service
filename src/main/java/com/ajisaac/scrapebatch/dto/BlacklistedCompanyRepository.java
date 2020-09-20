package com.ajisaac.scrapebatch.dto;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlacklistedCompanyRepository extends JpaRepository<BlacklistedCompany, Long> {
  List<BlacklistedCompany> findByName(String name);
}
