package gaun.apply.repository;

import gaun.apply.entity.MailFormData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MailFormRepository extends JpaRepository<MailFormData, Long> {
    MailFormData findByUsername(String username);
}
