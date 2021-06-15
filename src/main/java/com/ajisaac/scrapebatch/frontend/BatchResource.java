package com.ajisaac.scrapebatch.frontend;

import com.ajisaac.scrapebatch.dto.ScrapeJob;
import com.ajisaac.scrapebatch.scrape.ScrapingExecutorType;
import com.ajisaac.scrapebatch.service.BatchService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

@Path("/batch")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class BatchResource {

  private final BatchService batchJobService;
//  private final SimpMessagingTemplate template;

  public BatchResource(BatchService batchJobService
//                       SimpMessagingTemplate template
  ) {
    this.batchJobService = batchJobService;
//    this.template = template;
  }

  /**
   * Only used from postman
   */
  @POST
  @Path("/scrape-job")
  public Response createScrapeJob(ScrapeJob scrapeJob) {
    scrapeJob = batchJobService.createScrapeJob(scrapeJob);
    if (scrapeJob == null)
      return Response.status(400).entity(null).build();
    return Response.accepted(scrapeJob).build();
  }

  /**
   * Only used from postman
   */
  @POST
  @Path("/scrape-jobs")
  public Response createScrapeJobs(List<ScrapeJob> scrapeJobs) {
    List<ScrapeJob> createdJobs = batchJobService.createScrapeJobs(scrapeJobs);
    return Response.accepted().entity(createdJobs).build();
  }

  @GET
  @Path("/scrape-jobs")
  public List<ScrapeJob> getScrapeJobs() {
    return batchJobService.getAllScrapeJobs();
  }

  @GET
  @Path("/sites")
  public List<String> getSites() {
    List<String> sites = new ArrayList<>();
    for (ScrapingExecutorType i : ScrapingExecutorType.values())
      sites.add(i.toString());
    return sites;
  }

  @POST
  @Path("/scrape/{id}")
  public Response doScrape(@PathParam("id") Long id) {
    if (batchJobService.isCurrentlyScraping(id))
      return Response.ok("Already scraping this site.").build();
    String errMsg = batchJobService.doScrape(id);
    if (errMsg == null)
      return Response.ok("Batch scrape job " + id + " submitted").build();
    return Response.status(400).entity(errMsg).build();
  }

  @POST
  @Path("/stop-scrape/{id}")
  public Response stopScrape(@PathParam("id") Long id) {
    String msg = batchJobService.stopScraping(id);
    return Response.status(200).entity(msg).build();
  }
}
