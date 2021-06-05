package com.ajisaac.scrapebatch.frontend;

import com.ajisaac.scrapebatch.dto.HighlightWord;
import com.ajisaac.scrapebatch.dto.HighlightWordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/highlight")
public class HighlightWordResource {


  private final HighlightWordRepository repository;

  public HighlightWordResource(@Autowired HighlightWordRepository repository) {
    this.repository = repository;
  }

  @GetMapping("/all")
  public ResponseEntity<List<HighlightWord>> getAll() {
    return new ResponseEntity<>(repository.findAll(), HttpStatus.OK);
  }

  @PostMapping("/add")
  public ResponseEntity<List<HighlightWord>> addWord(@RequestBody HighlightWord word) {
    if (word.getName() == null || word.getName().isBlank())
      return getAll();

    var words = repository.findAll();
    for (HighlightWord w : words)
      if (w.getName().toLowerCase(Locale.ROOT).equals(word.getName().toLowerCase(Locale.ROOT)))
        return getAll();

    repository.save(word);
    return getAll();
  }
}
