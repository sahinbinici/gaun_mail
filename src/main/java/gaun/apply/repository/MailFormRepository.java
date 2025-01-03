package gaun.apply.repository;

import gaun.apply.entity.MailFormData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MailFormRepository extends JpaRepository<MailFormData, Long> {
    MailFormData findByUsername(String username);
    long countByStatus(boolean status);
    List<MailFormData> findTop10ByOrderByApplyDateDesc();
}
