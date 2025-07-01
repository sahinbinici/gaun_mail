package gaun.apply.config.db;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(
        basePackages = "gaun.apply.domain.user.entity",
        entityManagerFactoryRef = "dbIdariEntityManagerFactory",
        transactionManagerRef = "dbIdariTransactionManager"
)
public class DbIdariConfig {
    @Bean
    @ConfigurationProperties("spring.datasource.dbidari")
    public DataSource dbIdariDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean dbIdariEntityManagerFactory(
            EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(dbIdariDataSource())
                .packages("gaun.apply.domain.user.entity")
                .persistenceUnit("dbidari")
                .build();
    }

    @Bean
    public PlatformTransactionManager dbIdariTransactionManager(
            @Qualifier("dbIdariEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}

