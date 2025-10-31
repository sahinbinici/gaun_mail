package com.mobilewebservices.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class RemoteConfigDataSourceConfig {

    @Bean(name = "remoteConfigDataSource")
    public DataSource remoteConfigDataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:mariadb://localhost:3306/useful_links");
        dataSource.setUsername("root");
        dataSource.setPassword("sahinbey_");
        dataSource.setDriverClassName("org.mariadb.jdbc.Driver");
        dataSource.setPoolName("RemoteConfigHikariPool");
        dataSource.setMaximumPoolSize(5);
        dataSource.setMinimumIdle(2);
        dataSource.setConnectionTimeout(20000);
        return dataSource;
    }

    @Bean(name = "remoteConfigJdbcTemplate")
    public JdbcTemplate remoteConfigJdbcTemplate(@Qualifier("remoteConfigDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
