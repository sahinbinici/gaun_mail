package gaun.apply.repository.form;

import gaun.apply.entity.form.VpnFormData;
import org.springframework.stereotype.Repository;
import gaun.apply.entity.form.MailFormData;

import java.util.List;

@Repository
public interface MailFormRepository extends BaseFormRepository<MailFormData> {
    MailFormData findByUsername(String username);
    List<MailFormData> findTop10ByOrderByApplyDateDesc();
} 