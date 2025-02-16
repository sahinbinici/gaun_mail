package gaun.apply.repository.form;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import gaun.apply.entity.form.ServerSetupFormData;

public interface ServerSetupFormRepository extends JpaRepository<ServerSetupFormData, Long> {
    ServerSetupFormData findByTcKimlikNo(String tcKimlikNo);
    long countByStatus(boolean status);
    List<ServerSetupFormData> findTop10ByOrderByApplyDateDesc();
} 