package gaun.apply.repository.form;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import gaun.apply.entity.form.CloudAccountFormData;
import org.springframework.stereotype.Repository;

@Repository
public interface CloudAccountFormRepository extends BaseFormRepository<CloudAccountFormData> {
    CloudAccountFormData findByTcKimlikNo(String tcKimlikNo);
    long countByStatus(boolean status);
    List<CloudAccountFormData> findTop10ByOrderByApplyDateDesc();
} 