package com.ajisaac.scrapebatch.sites.nodeskco;

import com.ajisaac.scrapebatch.dto.JobPosting;
import com.ajisaac.scrapebatch.dto.ScrapeJob;
import com.ajisaac.scrapebatch.scrape.ScrapingExecutor;
import com.ajisaac.scrapebatch.scrape.SinglePageScrapingExecutor;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NodeskcoScrapingExecutorTest{

  @Test
  void testScrape() {
    NodeskcoScrapingExecutor scrapingExecutor = new NodeskcoScrapingExecutor();
    scrapingExecutor.scrape();
  }

}