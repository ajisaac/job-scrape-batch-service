package com.ajisaac.scrapebatch;

import org.junit.jupiter.api.Test;

class IndeedSearchUrlTest {

  @Test
  public void testUrlCreation() {

    String expected = "https://www.indeed.com/jobs?q=java&l=Portland%2C+OR&sort=date";
//    IndeedSearchUrl indeedSearchUrl = new IndeedSearchUrl("java", "Portland, OR", "25", "date", true);
//    String actual = indeedSearchUrl.getUri().toString();
//    assertEquals(expected, actual);
  }
}
