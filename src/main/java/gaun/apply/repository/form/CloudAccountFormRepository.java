package gaun.apply.repository.form;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import gaun.apply.entity.form.CloudAccountFormData;
import org.springframework.stereotype.Repository;

@Repository
public interface CloudAccountFormRepository extends BaseFormRepository<CloudAccountFormData> {
    List<CloudAccountFormData> findByApplyDateAfter(LocalDateTime date);
    List<CloudAccountFormData> findTop100ByOrderByApplyDateDesc();
}