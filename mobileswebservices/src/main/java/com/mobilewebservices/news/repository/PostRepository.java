package com.mobilewebservices.news.repository;

import com.mobilewebservices.news.dto.PostDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PostRepository {

    @Autowired
    @Qualifier("newsJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    public List<PostDto> findAll() {
        String sql = "SELECT ID as id, post_date as postDate, post_title as postTitle, " +
                    "post_content as postContent, post_name as postName, guid as haberLinki, post_type as postType " +
                    "FROM gaunhabermerkezivt.wp_posts";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(PostDto.class));
    }

    public PostDto findById(Long id) {
        String sql = "SELECT ID as id, post_date as postDate, post_title as postTitle, " +
                    "post_content as postContent, post_name as postName, guid as haberLinki, post_type as postType " +
                    "FROM gaunhabermerkezivt.wp_posts WHERE ID = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, new BeanPropertyRowMapper<>(PostDto.class));
    }

    public List<PostDto> findByPostType(String postType) {
        String sql = "SELECT ID as id, post_date as postDate, post_title as postTitle, " +
                    "post_content as postContent, post_name as postName, guid as haberLinki, post_type as postType " +
                    "FROM gaunhabermerkezivt.wp_posts WHERE post_type = ?";
        return jdbcTemplate.query(sql, new Object[]{postType}, new BeanPropertyRowMapper<>(PostDto.class));
    }

    public List<PostDto> findByPostNameContaining(String keyword) {
        String sql = "SELECT ID as id, post_date as postDate, post_title as postTitle, " +
                    "post_content as postContent, post_name as postName, guid as haberLinki, post_type as postType " +
                    "FROM gaunhabermerkezivt.wp_posts WHERE post_name LIKE ?";
        return jdbcTemplate.query(sql, new Object[]{"%" + keyword + "%"}, new BeanPropertyRowMapper<>(PostDto.class));
    }

    public List<PostDto> findLastMontPosts() {
        String sql = "SELECT ID as id, post_date as postDate, post_title as postTitle, " +
                    "post_content as postContent, post_name as postName,guid as haberLinki, post_type as postType " +
                    "FROM gaunhabermerkezivt.wp_posts ORDER BY post_date DESC LIMIT 30";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(PostDto.class));
    }

    public List<PostDto> findLatest10Posts() {
        String sql = "SELECT ID as id, post_date as postDate, post_title as postTitle, " +
                    "post_content as postContent, post_name as postName, guid as haberLinki, post_type as postType " +
                    "FROM gaunhabermerkezivt.wp_posts ORDER BY post_date DESC LIMIT 10";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(PostDto.class));
    }
}
