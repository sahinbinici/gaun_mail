package gaun.apply.config.db;

import gaun.apply.domain.user.repository.CalyerKurumdisiRepository;
import gaun.apply.domain.user.repository.StaffRepository;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;

@Configuration
public class RepositoryBeanConfig {

    @Bean(name = "personelIdariRepository")
    public StaffRepository personelIdariRepository(
            @Qualifier("dbIdariEntityManagerFactory") EntityManagerFactory emf) {
        return new JpaRepositoryFactory(emf.createEntityManager()).getRepository(StaffRepository.class);
    }

    @Bean(name = "personelAkademikRepository")
    public StaffRepository personelAkademikRepository(
            @Qualifier("dbAkademikEntityManagerFactory") EntityManagerFactory emf) {
        return new JpaRepositoryFactory(emf.createEntityManager()).getRepository(StaffRepository.class);
    }

    @Bean(name = "personelSurekliRepository")
    public StaffRepository personelSurekliRepository(
            @Qualifier("dbSurekliEntityManagerFactory") EntityManagerFactory emf) {
        return new JpaRepositoryFactory(emf.createEntityManager()).getRepository(StaffRepository.class);
    }

    @Bean(name = "calyerKurumdisiAkademikRepository")
    public CalyerKurumdisiRepository calyerKurumdisiAkademikRepository(
            @Qualifier("dbAkademikEntityManagerFactory") EntityManagerFactory emf) {
        return new JpaRepositoryFactory(emf.createEntityManager()).getRepository(CalyerKurumdisiRepository.class);
    }
}
