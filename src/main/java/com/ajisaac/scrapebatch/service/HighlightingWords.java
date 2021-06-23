package com.ajisaac.scrapebatch.service;

import com.ajisaac.scrapebatch.dto.HighlightWord;
import com.ajisaac.scrapebatch.dto.JobPosting;

import java.util.List;
import java.util.Locale;

public class HighlightingWords {

  public static void highlightJobDescriptions(List<JobPosting> postings, List<HighlightWord> buzzwords) {

    buzzwords.sort((word1, word2) -> {
      var w1 = word1.getName().toLowerCase(Locale.ROOT);
      var w2 = word2.getName().toLowerCase(Locale.ROOT);

      if (w1.equals(w2))
        return 0;
      if (w1.length() < w2.length() && w2.startsWith(w1))
        return 1;
      if (w2.length() < w1.length() && w1.startsWith(w2))
        return -1;

      return w1.compareTo(w2);

    });

    for (JobPosting jp : postings) {
      var desc = jp.getDescription();
      desc = highlightJobDescription(desc, buzzwords);
      jp.setDescription(desc);
    }
  }

  private static String highlightJobDescription(String desc, List<HighlightWord> buzzwords) {
    if (desc == null || buzzwords == null || buzzwords.isEmpty())
      return desc;


    for (HighlightWord buzzword : buzzwords) {
      // todo make this much better
      // todo maybe apply this only when updating highlight words
      // really want to hit the edge cases
      var word = buzzword.getName();
      var replaceWord = " <span class=\"highlight\">" + word + "</span>";
      desc = desc.replaceAll("(?i)" + " " + word, replaceWord);
    }

    return desc;
  }
}
