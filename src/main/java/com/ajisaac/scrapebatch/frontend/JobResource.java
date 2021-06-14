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
  public List<JobPosting> getAllJobs(Filtering filtering) {
    return jobService.getAllJobs(filtering);
  }

  @GET
  @Path("/backup")
  public List<JobPosting> backup() {
    return getAllJobs(null);
  }

  @POST
  @Path("/new/angelco")
  public List<JobPosting> addAngelCoJobPosting(JobPosting posting) {
    jobService.addAngelCoJobPosting(posting);
    return getAllJobs(null);
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
