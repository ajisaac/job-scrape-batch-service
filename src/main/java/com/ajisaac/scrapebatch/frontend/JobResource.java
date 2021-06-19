package com.ajisaac.scrapebatch.frontend;

import com.ajisaac.scrapebatch.dto.*;
import com.ajisaac.scrapebatch.service.JobService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

// todo add validations
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/jobs")
public class JobResource {
  private final JobService jobService;

  public JobResource(JobService jobService) {
    this.jobService = jobService;
  }

  @POST
  @Path("/all")
  public PostingsAndFilter getAllJobs(Filtering filtering) {
    return jobService.getAllJobs(filtering);
  }

//  @GET
//  @Path("/backup")
//  public List<JobPosting> backup() {
//    return getAllJobs(null);
//  }
//
//  @POST
//  @Path("/new/angelco")
//  public List<JobPosting> addAngelCoJobPosting(JobPosting posting) {
//    jobService.addAngelCoJobPosting(posting);
//    return getAllJobs(null);
//  }

  @POST
  @Path("/status/{id}/{status}")
  public PostingsAndFilter updateJobStatus(@PathParam("id") Long id,
                                           @PathParam("status") String status,
                                           Filtering filter) {
    jobService.updateJobStatus(id, status);
    return getAllJobs(filter);
  }
}
