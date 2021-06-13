# TODO
- Set up devops with Github and AWS
- filter by job source
- STACK OVERFLOW
	- parse the tags from "technologies" section
	- parse the "About this job" section
- flatten out the structure of the companies in view
	- We want each job to be viewed individually
- Make it easier to read, the design is a bit difficult currently
- Cache jobs server side, mirroring the database, so we can more performantly do comparisons.
- Get rid of summary in favor of job description.
- Clean the description when parsing to remove useless elements.
- Parse dates correctly to represent a point in time, and be displayed nicely.
- Enchance the page for displaying other job boards that we don't scrape.
- Get all the old functionality back and into react.
- Add the batch page to control job scrapes and update progress in real time.
- Handle duplicates for when we scrape a second time.
- Normalize the job descriptions.
- Handle the attempted creation of two identical scrapers.
- Add a scrapeall button
- Enhance logging 
- Restrict job postings and scraping to just jobs posted this month
- Can tell if a job is expired and remove it
- filter by job
- fix all the react stuff to look good and replicate what we had before
- fix up all the UI to look good again
- figure out theming
- make this thing truly production ready with a set of quality tools for linting and such
	- linting
	- checkstyle
	- run various tests
	- automated builds
	- code coverage
	- security scans
- log out to ELK stack
- Ability to add jobs from job boards we don't scrape.
- Search list based upon words, so we can search for a word then promptly ignore all those jobs.
- Add more refined filtering, over tags and such.
- Highlight buzzwords
- Highlight search words
- Set up one click scrape. Push a button, everything starts scraping.
- We need something to detect that our scrapers still work as expected
	or to otherwise warn us. This is incase the site devs decided to
	change something we rely on.
- Refined control of scrapers
- Put everything under test.
- SmokeTests
- Refactor the reducers to be more performant and cleaner.
- Write a deployment, test, and build script.
- Refactor scrapers to be SRP, or rewrite in Golang
- Handle non dev jobs and get the option to scrape non dev jobs.
- Go over our scrapers and make sure they're grabbing anything interesting.
- For companies maybe include a glassdoor link.
- Write the non javascript scrapers.

### bugs
- Handle that weird overlapping text bug.

# DONE
- Fix the performance issues with rendering so many elements on status change/page navigation.
	- solution \#1 - Only display up to 10 companies at any given time.
		- with this solution, we would avoid a really large deep copy.
		- we would avoid a huge number of allocations.
		- we would avoid rendering a huge amount of things on the screen at one time.
		- we would avoid the nursery being full issue.


# scrapers
indeed.com - yep.
remotive.io - no javascript needed.
weworkremotely.com - no javascript needed.
remote.co - no javascript needed.
remoteok.io - Good postings, no javascript needed.
sitepoint.com - easy to scape, nice jobs, no javascript needed.
stackoverflow.com - doesn't require js. Nice jobs. 
workingnomads.co - Has API.

# in progress
nodesk.co - javascript required, decent job selection, site will be difficult to scrape,
	but the results are worth it.
jobs.github.com - doesn't require js. Nice jobs.
flexjobs.com - possibly easy to scrape, doesn't seem to require javascript.
workew.com - no javascript needed. Low job postings amount. Easy to scrape.
news.ycombinator.com/jobs - small selection but a daily selection.
remotees.net - decent european job board.
angel.co - Will use in person, possibly write javascript based scraper plugin.
100telecommutejobs.com - decent jobs, needs javascript, low amount of jobs.
remoters.net - slow as shit, okay job selection.
dynamitejobs.com - requires javascript, seems okay, not easy to scrape.

# no good - Maybe do these by hand later if we really get to that point.
skipthedrive.com - Maybe maybe not. Has a php redirect to tracker which is blocked.
jobspresso.co - Low amount of postings. Requires javascript. Not worth the effort.
themuse.com - Low job selection, requires javascript, not worth the effort.
outsourcely.com - Behind login wall. Contract work.
virtualvocations.com - Shitty job selection, behind login wall.
remotees.com - job aggregation of the other good boards that we already scrape.
monster - No remote only option, mostly just junk, tons of contractor crap
careerbuilder - Similar to monster, no remote only options, tons of contractor crap
dice.com - The worst of the contractor crap.
remote4me.com - javascript needed. decent jobs. appears to have stopped scraping.
remotelyawesomejobs.com - no javascript needed. Decent list. Slow as shit, times out. 
