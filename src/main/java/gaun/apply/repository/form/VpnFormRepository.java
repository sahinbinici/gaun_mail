package gaun.apply.repository.form;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import gaun.apply.entity.form.VpnFormData;

public interface VpnFormRepository extends JpaRepository<VpnFormData, Long> {
    VpnFormData findByTcKimlikNo(String tcKimlikNo);
    long countByStatus(boolean status);
    List<VpnFormData> findTop10ByOrderByApplyDateDesc();
} 