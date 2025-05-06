package gaun.apply.repository.form;

import java.util.List;
import java.time.LocalDateTime;

import org.springframework.stereotype.Repository;
import gaun.apply.entity.form.MailFormData;
import gaun.apply.enums.ApplicationStatusEnum;

@Repository
public interface MailFormRepository extends BaseFormRepository<MailFormData> {
    MailFormData findByUsername(String username);
    MailFormData findByTcKimlikNo(String tcKimlikNo);
    List<MailFormData> findTop10ByOrderByApplyDateDesc();
    List<MailFormData> findTop100ByOrderByApplyDateDesc();
    List<MailFormData> findByApplyDateAfter(LocalDateTime date);
    void deleteById(Long id);
    List<MailFormData> findByApplicationStatus(ApplicationStatusEnum applicationStatus);
}