package com.ajisaac.scrapebatch.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class HighlightWord {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @JsonIgnoreProperties(ignoreUnknown = true)
  private long id;
  @Column(nullable = false)
  private String name;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

}
