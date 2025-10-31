package gaun.apply.config.db;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Objects;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = {"gaun.apply.domain.user.repository", "gaun.apply.domain.mail.repository", "gaun.apply.domain.eduroam.repository", "gaun.apply.infrastructure.repository"},
        entityManagerFactoryRef = "dbSurekliEntityManagerFactory",
        transactionManagerRef = "dbSurekliTransactionManager"
)
public class DbSurekliConfig {

    @Primary
    @Bean(name = "dbSurekliProperties")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSourceProperties dbSurekliProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean(name = "dbSurekliDataSource")
    public DataSource dbSurekliDataSource(@Qualifier("dbSurekliProperties") DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().build();
    }

    @Primary
    @Bean(name = "dbSurekliEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean dbSurekliEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("dbSurekliDataSource") DataSource dataSource
    ) {
        return builder
                .dataSource(dataSource)
                .packages("gaun.apply.domain.user", "gaun.apply.domain.mail", "gaun.apply.domain.eduroam", "gaun.apply.infrastructure")
                .persistenceUnit("dbSurekli")
                .build();
    }

    @Primary
    @Bean(name = "dbSurekliTransactionManager")
    public PlatformTransactionManager dbSurekliTransactionManager(
            @Qualifier("dbSurekliEntityManagerFactory") LocalContainerEntityManagerFactoryBean entityManagerFactory
    ) {
        return new JpaTransactionManager(Objects.requireNonNull(entityManagerFactory.getObject()));
    }
}

