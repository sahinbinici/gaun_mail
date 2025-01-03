package gaun.apply.repository.form;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import gaun.apply.entity.form.WirelessNetworkFormData;

public interface WirelessNetworkFormRepository extends JpaRepository<WirelessNetworkFormData, Long> {
    List<WirelessNetworkFormData> findTop10ByOrderByApplyDateDesc();
    long countByStatus(boolean status);
} 