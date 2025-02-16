package gaun.apply.repository.form;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import gaun.apply.entity.form.WebAcademicFormData;

public interface WebAcademicFormRepository extends JpaRepository<WebAcademicFormData, Long> {
    WebAcademicFormData findByTcKimlikNo(String tcKimlikNo);
    long countByStatus(boolean status);
    List<WebAcademicFormData> findTop10ByOrderByApplyDateDesc();
} 