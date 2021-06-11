package com.ajisaac.scrapebatch.frontend;

import com.ajisaac.scrapebatch.dto.HighlightWord;
import com.ajisaac.scrapebatch.dto.HighlightWordRepository;

import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Locale;

@Path("/highlight")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class HighlightWordResource {

  private final HighlightWordRepository repository;

  public HighlightWordResource(HighlightWordRepository repository) {
    this.repository = repository;
  }

  @GET
  @Path("/all")
  @Transactional
  public List<HighlightWord> getAll() {
    return repository.findAll().list();
  }

  @POST
  @Path("/add")
  @Transactional
  public List<HighlightWord> addWord(HighlightWord word) {
    if (word.getName() == null || word.getName().isBlank())
      return getAll();

    var words = repository.findAll().list();
    for (HighlightWord w : words)
      if (w.getName().toLowerCase(Locale.ROOT).equals(word.getName().toLowerCase(Locale.ROOT)))
        return getAll();

    repository.persist(word);
    return getAll();
  }
}
