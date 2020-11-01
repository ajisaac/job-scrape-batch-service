package com.ajisaac.scrapebatch.network;

import java.net.URI;
import java.net.URISyntaxException;

/** Grabs pages... */
public class PageGrabber {

  public static String grabPage(URI uri) {
    if (uri == null) {
      return null;
    }
    String ret = new PageRequest(uri).sendGet();
    if (ret == null || ret.isBlank()) {
      return null;
    }
    return ret;
  }

  public static String grabPage(String href){
    if (href == null || href.isBlank()) {
      return null;
    }
    try {
      URI uri = new URI(href);
      return grabPage(uri);
    } catch (URISyntaxException ex) {
      return null;
    }
  }
}
