package com.mobilewebservices.announcements.service;

import com.mobilewebservices.announcements.dto.ArticleDto;
import com.mobilewebservices.announcements.repository.ArticleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArticleService {

    private final ArticleRepository articleRepository;

    public ArticleService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    public List<ArticleDto> getAllArticles() {
        return articleRepository.findAll();
    }

    public ArticleDto getArticleById(Long id) {
        return articleRepository.findById(id);
    }

    public List<ArticleDto> getArticlesBySectionId(Long sectionId) {
        return articleRepository.findBySectionId(sectionId);
    }

    public List<ArticleDto> getArticlesByAuthorId(Long authorId) {
        return articleRepository.findByAuthorId(authorId);
    }

    public List<ArticleDto> getLatest10Articles() {
        return articleRepository.findLatest10Articles();
    }
}
