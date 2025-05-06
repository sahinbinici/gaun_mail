package gaun.apply.repository.form;

import gaun.apply.entity.form.BaseFormData;
import gaun.apply.enums.ApplicationStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface BaseFormRepository<T extends BaseFormData> extends JpaRepository<T, Long> {
    List<T> findByApplicationStatus(ApplicationStatusEnum status);
    long countByStatus(boolean status);
    List<T> findTop10ByOrderByApplyDateDesc();
    T findByTcKimlikNo(String tcKimlikNo);
}