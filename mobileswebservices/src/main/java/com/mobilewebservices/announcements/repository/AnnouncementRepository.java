package com.mobilewebservices.announcements.repository;

import com.mobilewebservices.announcements.dto.AnnouncementDto;
import com.mobilewebservices.common.dto.PageRequest;
import com.mobilewebservices.exception.DatabaseException;
import com.mobilewebservices.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AnnouncementRepository {

    private final JdbcTemplate jdbcTemplate;

    public AnnouncementRepository(@Qualifier("defaultJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<AnnouncementDto> findAll() {
        try {
            String sql = "SELECT ID as id, title, text, authorID as authorId, sectionID as sectionId FROM ae_articles";
            return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(AnnouncementDto.class));
        } catch (DataAccessException e) {
            throw new DatabaseException("Error retrieving all announcements", e);
        }
    }

    public AnnouncementDto findById(Long id) {
        try {
            String sql = "SELECT ID as id, title, text, authorID as authorId, sectionID as sectionId FROM ae_articles WHERE ID = ?";
            return jdbcTemplate.queryForObject(sql, new Object[]{id}, new BeanPropertyRowMapper<>(AnnouncementDto.class));
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("Announcement", "id", id);
        } catch (DataAccessException e) {
            throw new DatabaseException("Error retrieving announcement with id: " + id, e);
        }
    }

    public List<AnnouncementDto> findBySectionId(Long sectionId) {
        try {
            String sql = "SELECT ID as id, title, text, authorID as authorId, sectionID as sectionId FROM ae_articles WHERE sectionID = ?";
            return jdbcTemplate.query(sql, new Object[]{sectionId}, new BeanPropertyRowMapper<>(AnnouncementDto.class));
        } catch (DataAccessException e) {
            throw new DatabaseException("Error retrieving announcements for section: " + sectionId, e);
        }
    }

    public List<AnnouncementDto> findByAuthorId(Long authorId) {
        try {
            String sql = "SELECT ID as id, title, text, authorID as authorId, sectionID as sectionId FROM ae_articles WHERE authorID = ?";
            return jdbcTemplate.query(sql, new Object[]{authorId}, new BeanPropertyRowMapper<>(AnnouncementDto.class));
        } catch (DataAccessException e) {
            throw new DatabaseException("Error retrieving announcements for author: " + authorId, e);
        }
    }

    public List<AnnouncementDto> findLatest10Announcements() {
        try {
            String sql = "SELECT ID as id, title, text, authorID as authorId, sectionID as sectionId " +
                        "FROM ae_articles ORDER BY ID DESC LIMIT 10";
            return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(AnnouncementDto.class));
        } catch (DataAccessException e) {
            throw new DatabaseException("Error retrieving latest announcements", e);
        }
    }

    public List<AnnouncementDto> findAllWithPagination(PageRequest pageRequest) {
        try {
            String sql = "SELECT ID as id, title, text, authorID as authorId, sectionID as sectionId " +
                        "FROM ae_articles ORDER BY " + pageRequest.getSortClause() +
                        " LIMIT ? OFFSET ?";
            return jdbcTemplate.query(sql, new Object[]{pageRequest.getLimit(), pageRequest.getOffset()},
                                    new BeanPropertyRowMapper<>(AnnouncementDto.class));
        } catch (DataAccessException e) {
            throw new DatabaseException("Error retrieving announcements with pagination", e);
        }
    }

    public long countAll() {
        try {
            return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM ae_articles", Long.class);
        } catch (DataAccessException e) {
            throw new DatabaseException("Error counting announcements", e);
        }
    }

    public List<AnnouncementDto> findBySectionIdWithPagination(Long sectionId, PageRequest pageRequest) {
        try {
            String sql = "SELECT ID as id, title, text, authorID as authorId, sectionID as sectionId " +
                        "FROM ae_articles WHERE sectionID = ? ORDER BY " + pageRequest.getSortClause() +
                        " LIMIT ? OFFSET ?";
            return jdbcTemplate.query(sql, new Object[]{sectionId, pageRequest.getLimit(), pageRequest.getOffset()},
                                    new BeanPropertyRowMapper<>(AnnouncementDto.class));
        } catch (DataAccessException e) {
            throw new DatabaseException("Error retrieving announcements by section with pagination", e);
        }
    }

    public long countBySectionId(Long sectionId) {
        try {
            return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM ae_articles WHERE sectionID = ?",
                                              new Object[]{sectionId}, Long.class);
        } catch (DataAccessException e) {
            throw new DatabaseException("Error counting announcements by section", e);
        }
    }

    public List<AnnouncementDto> findAnnouncementsFromLastMonth() {
        try {
            String sql = "SELECT ID as id, title, text, authorID as authorId, sectionID as sectionId " +
                        "FROM ae_articles ORDER BY ID DESC LIMIT 30";
            return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(AnnouncementDto.class));
        } catch (DataAccessException e) {
            throw new DatabaseException("Error retrieving announcements from last month", e);
        }
    }
}
