package com.ajisaac.scrapebatch.dto;

import static com.google.common.base.Preconditions.checkNotNull;

public class Link {
  private String link;

  public Link(String link){
    checkNotNull(link);
    this.link = link;
  }

  public String getLink() {
    return link;
  }
}
