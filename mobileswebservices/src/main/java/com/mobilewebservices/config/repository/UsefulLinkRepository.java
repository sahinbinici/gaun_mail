package com.mobilewebservices.config.repository;

import com.mobilewebservices.config.dto.UsefulLinkDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class UsefulLinkRepository {

    private final JdbcTemplate jdbcTemplate;

    public UsefulLinkRepository(@Qualifier("remoteConfigJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<UsefulLinkDto> ROW_MAPPER = new RowMapper<UsefulLinkDto>() {
        @Override
        public UsefulLinkDto mapRow(ResultSet rs, int rowNum) throws SQLException {
            return UsefulLinkDto.builder()
                    .id(rs.getString("id"))
                    .title(rs.getString("title"))
                    .icon(rs.getString("icon"))
                    .url(rs.getString("url"))
                    .description(rs.getString("description"))
                    .color(rs.getString("color"))
                    .order(rs.getInt("order_index"))
                    .isActive(rs.getBoolean("is_active"))
                    .build();
        }
    };

    public List<UsefulLinkDto> findByIsActiveTrueOrderByOrderIndexAsc() {
        String sql = "SELECT * FROM useful_links WHERE is_active = TRUE ORDER BY order_index ASC";
        return jdbcTemplate.query(sql, ROW_MAPPER);
    }

    public List<UsefulLinkDto> findAllByOrderByOrderIndexAsc() {
        String sql = "SELECT * FROM useful_links ORDER BY order_index ASC";
        return jdbcTemplate.query(sql, ROW_MAPPER);
    }

    public Optional<UsefulLinkDto> findById(String id) {
        String sql = "SELECT * FROM useful_links WHERE id = ?";
        List<UsefulLinkDto> results = jdbcTemplate.query(sql, ROW_MAPPER, id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public boolean existsById(String id) {
        String sql = "SELECT COUNT(*) FROM useful_links WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    public int save(UsefulLinkDto dto) {
        String sql = "INSERT INTO useful_links (id, title, icon, url, description, color, order_index, is_active) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE " +
                     "title = VALUES(title), icon = VALUES(icon), url = VALUES(url), " +
                     "description = VALUES(description), color = VALUES(color), " +
                     "order_index = VALUES(order_index), is_active = VALUES(is_active)";

        return jdbcTemplate.update(sql,
                dto.getId(),
                dto.getTitle(),
                dto.getIcon(),
                dto.getUrl(),
                dto.getDescription(),
                dto.getColor(),
                dto.getOrder() != null ? dto.getOrder() : 0,
                dto.getIsActive() != null ? dto.getIsActive() : true);
    }

    public int update(String id, UsefulLinkDto dto) {
        String sql = "UPDATE useful_links SET title = ?, icon = ?, url = ?, description = ?, " +
                     "color = ?, order_index = ?, is_active = ? WHERE id = ?";

        return jdbcTemplate.update(sql,
                dto.getTitle(),
                dto.getIcon(),
                dto.getUrl(),
                dto.getDescription(),
                dto.getColor(),
                dto.getOrder() != null ? dto.getOrder() : 0,
                dto.getIsActive() != null ? dto.getIsActive() : true,
                id);
    }

    public int deleteById(String id) {
        String sql = "DELETE FROM useful_links WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    public int toggleStatus(String id) {
        String sql = "UPDATE useful_links SET is_active = NOT is_active WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }
}
