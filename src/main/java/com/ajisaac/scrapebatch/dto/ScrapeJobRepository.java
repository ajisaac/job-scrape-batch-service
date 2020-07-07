package com.ajisaac.scrapebatch.dto;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ScrapeJobRepository extends JpaRepository<ScrapeJob, Long> {
}
