package com.mobilewebservices.news.controller;

import com.mobilewebservices.news.dto.PostDto;
import com.mobilewebservices.news.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public ResponseEntity<List<PostDto>> getAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDto> getPostById(@PathVariable Long id) {
        return ResponseEntity.ok(postService.getPostById(id));
    }

    @GetMapping("/type/{postType}")
    public ResponseEntity<List<PostDto>> getPostsByType(@PathVariable String postType) {
        return ResponseEntity.ok(postService.getPostsByType(postType));
    }

    @GetMapping("/search")
    public ResponseEntity<List<PostDto>> searchPostsByName(@RequestParam String keyword) {
        return ResponseEntity.ok(postService.searchPostsByName(keyword));
    }

    @GetMapping("/lastMonth")
    public ResponseEntity<List<PostDto>> getLastMontPosts() {
        return ResponseEntity.ok(postService.getLastMontPosts());
    }

    @GetMapping("/latest")
    public ResponseEntity<List<PostDto>> getLatest10Posts() {
        return ResponseEntity.ok(postService.getLatest10Posts());
    }
}
