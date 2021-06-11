package com.ajisaac.scrapebatch.network;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class PageGrabber {

  private PageGrabber() {
  }

  public static String grabPage(URI uri) {
    if (uri == null)
      return null;

    var ret = new PageRequest(uri).sendGet();
    if (ret == null || ret.isBlank())
      return null;

    return ret;
  }

  public static String grabPage(String href) {
    if (href == null || href.isBlank())
      return null;

    try {
      var uri = new URI(href);
      return grabPage(uri);
    } catch (URISyntaxException ex) {
      return null;
    }
  }

  static class PageRequest {
    private final HttpClient httpClient;
    private final URI uri;

    public PageRequest(URI uri) {
      // todo do we need to create a new one of these every time or can we reuse?
      this.uri = uri;
      httpClient =
        HttpClient.newBuilder()
          .version(HttpClient.Version.HTTP_2)
          .followRedirects(HttpClient.Redirect.NORMAL)
          .build();
    }

    public String sendGet() {
      if (this.uri == null) {
        return null;
      }

      HttpRequest request =
        HttpRequest.newBuilder()
          .GET()
          .uri(uri)
          .setHeader(
            "User-Agent", "Mozilla/5.0 (Windows NT 10.0; rv:68.0) Gecko/20100101 Firefox/68.0")
          .build();

      try {
        HttpResponse<String> response =
          httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
      } catch (IOException | InterruptedException e) {
        return "";
      }
    }
  }
}
