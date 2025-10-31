package com.mobilewebservices.announcements.controller;

import com.mobilewebservices.announcements.dto.ArticleDto;
import com.mobilewebservices.announcements.service.ArticleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping
    public ResponseEntity<List<ArticleDto>> getAllArticles() {
        return ResponseEntity.ok(articleService.getAllArticles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArticleDto> getArticleById(@PathVariable Long id) {
        return ResponseEntity.ok(articleService.getArticleById(id));
    }

    @GetMapping("/section/{sectionId}")
    public ResponseEntity<List<ArticleDto>> getArticlesBySectionId(@PathVariable Long sectionId) {
        return ResponseEntity.ok(articleService.getArticlesBySectionId(sectionId));
    }

    @GetMapping("/author/{authorId}")
    public ResponseEntity<List<ArticleDto>> getArticlesByAuthorId(@PathVariable Long authorId) {
        return ResponseEntity.ok(articleService.getArticlesByAuthorId(authorId));
    }

    @GetMapping("/latest")
    public ResponseEntity<List<ArticleDto>> getLatest10Articles() {
        return ResponseEntity.ok(articleService.getLatest10Articles());
    }
}
