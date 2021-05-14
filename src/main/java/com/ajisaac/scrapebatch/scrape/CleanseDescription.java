package com.ajisaac.scrapebatch.scrape;

/**
 * Code to cleanse the job description. Fixes some linebreaks,
 * removes bad html, etc
 */
public final class CleanseDescription {

  public static String cleanse(String desc) {
    if (desc != null) {
      desc = replaceLineBreaks(desc);
      desc = removeStylingTags(desc);
      desc = removeScriptingTags(desc);
    }
    return desc;
  }

  private static String removeScriptingTags(String desc) {
    return desc;
  }

  private static String removeStylingTags(String desc) {
    return desc;
  }

  public static String replaceLineBreaks(String text) {
    var parts = text.split("\n");
    StringBuilder b = new StringBuilder();
    for (String part : parts) {
      b.append(part);
      b.append("<br/>");
    }
    return b.toString();
  }
}
