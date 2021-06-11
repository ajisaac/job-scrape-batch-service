package com.ajisaac.scrapebatch.dto;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ScrapeJobRepository implements PanacheRepository<ScrapeJob> {

}
