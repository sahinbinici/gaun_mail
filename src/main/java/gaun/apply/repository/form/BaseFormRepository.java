package gaun.apply.repository.form;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import gaun.apply.entity.form.BaseFormData;

@NoRepositoryBean
public interface BaseFormRepository<T extends BaseFormData> extends JpaRepository<T, Long> {
    T findByTcKimlikNo(String tcKimlikNo);
    long countByStatus(boolean status);
    List<T> findTop10ByOrderByApplyDateDesc();
} 