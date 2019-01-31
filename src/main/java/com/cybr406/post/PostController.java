package com.cybr406.post;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class PostController {
  
  @Autowired
  PostRepository postRepository;
  
  @PostMapping("/posts")
  public ResponseEntity<Post> createPost(@RequestBody Post post) {
    return new ResponseEntity<>(postRepository.save(post), HttpStatus.CREATED);
  }
  
  @GetMapping("/posts")
  public Page<Post> getPosts(Pageable pageable) {
    return postRepository.findAll(pageable);
  }
  
  @GetMapping("/posts/{id}")
  public ResponseEntity<Post> getPost(@PathVariable Long id) {
    return postRepository.findById(id)
        .map(post -> new ResponseEntity<>(post, HttpStatus.OK))
        .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }
  
}
