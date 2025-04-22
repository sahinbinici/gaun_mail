package gaun.apply.repository.form;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gaun.apply.entity.form.MailFormData;

@Repository
public interface MailFormRepository extends JpaRepository<MailFormData, Long> {
    MailFormData findByUsername(String username);
    MailFormData findByTcKimlikNo(String tcKimlikNo);
    List<MailFormData> findTop10ByOrderByApplyDateDesc();
    void deleteById(Long id);
} 