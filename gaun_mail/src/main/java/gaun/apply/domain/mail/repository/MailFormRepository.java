package gaun.apply.domain.mail.repository;

import java.util.List;
import java.time.LocalDateTime;

import org.springframework.stereotype.Repository;
import gaun.apply.domain.mail.entity.MailFormData;
import gaun.apply.common.enums.ApplicationStatusEnum;
import gaun.apply.domain.common.BaseFormRepository;

@Repository
public interface MailFormRepository extends BaseFormRepository<MailFormData> {
    // Remove this method since username field doesn't exist
    // MailFormData findByUsername(String username);
    
    // Use existing methods instead
    MailFormData findByTcKimlikNo(String tcKimlikNo);
    List<MailFormData> findAllByTcKimlikNo(String tcKimlikNo);
    MailFormData findByOgrenciNo(String ogrenciNo);
    
    List<MailFormData> findTop10ByOrderByApplyDateDesc();
    List<MailFormData> findTop100ByOrderByApplyDateDesc();
    List<MailFormData> findByApplyDateAfter(LocalDateTime date);
    void deleteById(Long id);
    List<MailFormData> findByApplicationStatus(ApplicationStatusEnum applicationStatus);
}