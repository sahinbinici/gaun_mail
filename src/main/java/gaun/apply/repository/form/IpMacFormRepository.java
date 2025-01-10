package gaun.apply.repository.form;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import gaun.apply.entity.form.IpMacFormData;

public interface IpMacFormRepository extends JpaRepository<IpMacFormData, Long> {
    IpMacFormData findByTcKimlikNo(String tcKimlikNo);
    long countByStatus(boolean status);
    List<IpMacFormData> findTop10ByOrderByApplyDateDesc();
} 