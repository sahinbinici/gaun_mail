package com.mobilewebservices.config.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@Slf4j
public class AppConfigRepository {

    private final JdbcTemplate jdbcTemplate;

    public AppConfigRepository(@Qualifier("remoteConfigJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Map<String, Object>> findByConfigKey(String configKey) {
        String sql = "SELECT * FROM app_config WHERE config_key = ?";
        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, configKey);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public boolean existsByConfigKey(String configKey) {
        String sql = "SELECT COUNT(*) FROM app_config WHERE config_key = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, configKey);
        return count != null && count > 0;
    }

    public int save(String configKey, String configValue, String version) {
        String sql = "INSERT INTO app_config (config_key, config_value, version) VALUES (?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE config_value = VALUES(config_value), version = VALUES(version)";
        return jdbcTemplate.update(sql, configKey, configValue, version);
    }

    public int updateVersion(String configKey, String version) {
        String sql = "UPDATE app_config SET version = ? WHERE config_key = ?";
        return jdbcTemplate.update(sql, version, configKey);
    }
}
