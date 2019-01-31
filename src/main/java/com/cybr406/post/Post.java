package com.cybr406.post;

import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
public class Post {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;
  
  String author;
  
  @Lob
  @Type(type = "org.hibernate.type.TextType")
  String content;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }
  
}
