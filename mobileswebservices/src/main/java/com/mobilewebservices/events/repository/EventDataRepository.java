package com.mobilewebservices.events.repository;

import com.mobilewebservices.events.dto.EventDataDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EventDataRepository {

    private final JdbcTemplate jdbcTemplate;

    public EventDataRepository(@Qualifier("eventsJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<EventDataDto> findAll() {
        String sql = "SELECT id, year, mon as month, date, datetime as dateTime, title, content, `rank`, sender FROM data";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(EventDataDto.class));
    }

    public EventDataDto findById(Integer id) {
        String sql = "SELECT id, year, mon as month, date, datetime as dateTime, title, content, `rank`, sender FROM data WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, new BeanPropertyRowMapper<>(EventDataDto.class));
    }

    public List<EventDataDto> findByYear(Integer year) {
        String sql = "SELECT id, year, mon as month, date, datetime as dateTime, title, content, `rank`, sender FROM data WHERE year = ?";
        return jdbcTemplate.query(sql, new Object[]{year}, new BeanPropertyRowMapper<>(EventDataDto.class));
    }

    public List<EventDataDto> findBySender(String sender) {
        String sql = "SELECT id, year, mon as month, date, datetime as dateTime, title, content, `rank`, sender FROM data WHERE sender = ?";
        return jdbcTemplate.query(sql, new Object[]{sender}, new BeanPropertyRowMapper<>(EventDataDto.class));
    }

    public List<EventDataDto> findByDateRange(String startDate, String endDate) {
        String sql = "SELECT id, year, mon as month, date, datetime as dateTime, title, content, `rank`, sender " +
                    "FROM data WHERE datetime BETWEEN ? AND ?";
        return jdbcTemplate.query(sql, new Object[]{startDate, endDate}, new BeanPropertyRowMapper<>(EventDataDto.class));
    }

    public List<EventDataDto> findLatest10Events() {
        String sql = "SELECT id, year, mon as month, date, datetime as dateTime, title, content, `rank`, sender " +
                    "FROM data WHERE DATE(datetime) >= CURDATE() ORDER BY datetime ASC LIMIT 10";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(EventDataDto.class));
    }

    public List<EventDataDto> findEventsFromLastMonth() {
        String sql = "SELECT id, year, mon as month, date, datetime as dateTime, title, content, `rank`, sender " +
                    "FROM data WHERE DATE(datetime) >= CURDATE() " +
                    "AND DATE(datetime) <= DATE_ADD(CURDATE(), INTERVAL 1 MONTH) ORDER BY datetime ASC";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(EventDataDto.class));
    }
}
