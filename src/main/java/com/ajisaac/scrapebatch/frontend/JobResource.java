package com.ajisaac.scrapebatch.frontend;

import com.ajisaac.scrapebatch.dto.*;

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

  @GET
  @Path("/all")
  public List<JobPosting> getAllJobs() {
    return jobService.getAllJobs();
  }

  @GET
  @Path("/all/bycompany")
  public Companies getAllJobsByCompany() {
    return jobService.getAllJobsByCompany();
  }

  @GET
  @Path("/backup")
  public Companies backup() {
    return getAllJobsByCompany();
  }

  @POST
  @Path("/new/angelco")
  public Companies addAngelCoJobPosting(JobPosting posting) {
    jobService.addAngelCoJobPosting(posting);
    return getAllJobsByCompany();
  }

  @PUT
  @Path("/status/{id}/{status}")
  public Response updateJobStatus(@PathParam("id") Long id,
                                  @PathParam("status") String status) {
    var jobPosting = jobService.updateJobStatus(id, status);
    if (jobPosting == null)
      return Response.status(400, "Unable to update status.").build();
    return Response.ok(jobPosting).build();
  }

  @PUT
  @Path("/status/multiple/{status}")
  public Response updateMultipleJobStatuses(List<Long> jobStatuses,
                                            @PathParam("status") String status) {
    var jobPostings = jobService.updateMultipleJobStatuses(jobStatuses, status);
    return Response.ok(jobPostings).build();
  }
}
