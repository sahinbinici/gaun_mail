package com.mobilewebservices.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class DefaultDataSourceConfig {

    @Primary
    @Bean(name = "defaultDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.default")
    public DataSource defaultDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "defaultJdbcTemplate")
    public JdbcTemplate defaultJdbcTemplate(DataSource defaultDataSource) {
        return new JdbcTemplate(defaultDataSource);
    }
}
