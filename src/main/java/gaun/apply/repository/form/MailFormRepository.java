package gaun.apply.repository.form;

import org.springframework.stereotype.Repository;
import gaun.apply.entity.form.MailFormData;

@Repository
public interface MailFormRepository extends BaseFormRepository<MailFormData> {
    MailFormData findByUsername(String username);
} 