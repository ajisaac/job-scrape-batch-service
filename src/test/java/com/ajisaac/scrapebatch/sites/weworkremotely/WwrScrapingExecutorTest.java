package com.ajisaac.scrapebatch.sites.weworkremotely;

import com.ajisaac.scrapebatch.dto.JobPosting;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WwrScrapingExecutorTest {

  private WwrScrapingExecutor wwrScrapingExecutor;

  @BeforeEach
  void setUp() {
    wwrScrapingExecutor = new WwrScrapingExecutor();
  }

  @Test
  void parseMainPageTest() throws IOException {
    String mainPage = getMainPage();
    List<JobPosting> jobPostings = wwrScrapingExecutor.parseMainPage(mainPage);
  }

  private String getMainPage() throws IOException {
    Path fileName = Path.of("src/test/resources/WwrMainPageTest.txt");
    return Files.readString(fileName);
  }
}
