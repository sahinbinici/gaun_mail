package com.mobilewebservices.announcements.repository;

import com.mobilewebservices.announcements.dto.ArticleDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ArticleRepository {

    private final JdbcTemplate jdbcTemplate;

    public ArticleRepository(@Qualifier("defaultJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<ArticleDto> findAll() {
        String sql = "SELECT ID as id, title, text, authorID as authorId, sectionID as sectionId FROM ae_articles";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(ArticleDto.class));
    }

    public ArticleDto findById(Long id) {
        String sql = "SELECT ID as id, title, text, authorID as authorId, sectionID as sectionId FROM ae_articles WHERE ID = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, new BeanPropertyRowMapper<>(ArticleDto.class));
    }

    public List<ArticleDto> findBySectionId(Long sectionId) {
        String sql = "SELECT ID as id, title, text, authorID as authorId, sectionID as sectionId FROM ae_articles WHERE sectionID = ?";
        return jdbcTemplate.query(sql, new Object[]{sectionId}, new BeanPropertyRowMapper<>(ArticleDto.class));
    }

    public List<ArticleDto> findByAuthorId(Long authorId) {
        String sql = "SELECT ID as id, title, text, authorID as authorId, sectionID as sectionId FROM ae_articles WHERE authorID = ?";
        return jdbcTemplate.query(sql, new Object[]{authorId}, new BeanPropertyRowMapper<>(ArticleDto.class));
    }

    public List<ArticleDto> findLatest10Articles() {
        String sql = "SELECT ID as id, title, text, authorID as authorId, sectionID as sectionId " +
                    "FROM ae_articles ORDER BY ID DESC LIMIT 10";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(ArticleDto.class));
    }
}
