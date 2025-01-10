package gaun.apply.repository.form;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import gaun.apply.entity.form.CloudAccountFormData;

public interface CloudAccountFormRepository extends JpaRepository<CloudAccountFormData, Long> {
    CloudAccountFormData findByTcKimlikNo(String tcKimlikNo);
    long countByStatus(boolean status);
    List<CloudAccountFormData> findTop10ByOrderByApplyDateDesc();
} 