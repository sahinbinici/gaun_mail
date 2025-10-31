package com.mobilewebservices.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.sql.DataSource;

@Configuration
public class EventsDataSourceConfig {

    @Bean(name = "eventsDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.events")
    public DataSource eventsDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "eventsJdbcTemplate")
    public JdbcTemplate eventsJdbcTemplate(@Qualifier("eventsDataSource") DataSource eventsDataSource) {
        return new JdbcTemplate(eventsDataSource);
    }
}
