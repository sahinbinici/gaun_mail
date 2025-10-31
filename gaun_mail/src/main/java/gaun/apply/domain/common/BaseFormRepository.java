package gaun.apply.domain.common;

import gaun.apply.common.enums.ApplicationStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface BaseFormRepository<T extends BaseFormData> extends JpaRepository<T, Long> {
    List<T> findByApplicationStatus(ApplicationStatusEnum status);
    List<T> findTop10ByOrderByApplyDateDesc();
    T findByTcKimlikNo(String tcKimlikNo);
}
