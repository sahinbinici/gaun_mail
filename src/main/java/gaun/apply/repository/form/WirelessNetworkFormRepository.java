package gaun.apply.repository.form;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import gaun.apply.entity.form.WirelessNetworkFormData;

public interface WirelessNetworkFormRepository extends JpaRepository<WirelessNetworkFormData, Long> {
    WirelessNetworkFormData findByTcKimlikNo(String tcKimlikNo);
    long countByStatus(boolean status);
    List<WirelessNetworkFormData> findTop10ByOrderByApplyDateDesc();
} 