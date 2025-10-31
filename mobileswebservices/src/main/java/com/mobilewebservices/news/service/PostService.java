package com.mobilewebservices.news.service;

import com.mobilewebservices.news.dto.PostDto;
import com.mobilewebservices.news.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public List<PostDto> getAllPosts() {
        return postRepository.findAll();
    }

    public PostDto getPostById(Long id) {
        return postRepository.findById(id);
    }

    public List<PostDto> getPostsByType(String postType) {
        return postRepository.findByPostType(postType);
    }

    public List<PostDto> searchPostsByName(String keyword) {
        return postRepository.findByPostNameContaining(keyword);
    }

    public List<PostDto> getLastMontPosts() {
        return postRepository.findLastMontPosts();
    }

    public List<PostDto> getLatest10Posts() {
        return postRepository.findLatest10Posts();
    }
}
