# Job Scrape Batch System

The job scrape batch system is the system to handle the
continuous scraping of certain job sites. While this 
doesn't actually handle sending the HTTP requests, it
will handle all other aspects of the scraping. Another 
system handles the actual HTTP requests. 

### Todo

- [ ] Set up the automated testing system.
- [ ] Logging system to log store.
- [ ] Selective execution of batch jobs.
- [ ] Allows app to submit urls to http request system.
- [ ] Notification system to let the other systems know the details of any particular scrape job.
- [ ] Run through list of jobs every X minutes and look to see what new jobs need to be submitted. This X should be configurable in real time.
- [ ] Small scrapes in more frequent periods of time, larger scrapes are less frequent.
- [ ] Documentation system - We need to define our API's in a way where that documentation is updated during build.
- [ ] Orchestration - We have a bunch of microservices, want to have them all loosely coupled but easy to deploy. Will probably use kubernetes or some such thing.
- [ ] Monitoring - Want to monitor each of our microservices. We might end up doing this all on AWS and just eating the cost.
- [ ] Tracing - We want to trace all of our requests between each system. Much like UAPI has their tracing system.
- [ ] Deployment - Want a somewhat automated deployment system.

