package com.mobilewebservices.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class NewsDataSourceConfig {

    @Bean(name = "newsDataSource")
    public DataSource newsDataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:mysql://193.140.102.118:3306/gaunhabermerkezivt");
        dataSource.setUsername("SakinKullan11");
        dataSource.setPassword("Pelikan2025Mavi!!");
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setPoolName("NewsHikariPool");
        return dataSource;
    }

    @Bean(name = "newsJdbcTemplate")
    public JdbcTemplate newsJdbcTemplate(@Qualifier("newsDataSource") DataSource newsDataSource) {
        return new JdbcTemplate(newsDataSource);
    }
}
