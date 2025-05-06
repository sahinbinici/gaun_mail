package gaun.apply.repository.form;

import java.util.Optional;
import java.util.List;
import java.time.LocalDateTime;

import org.springframework.stereotype.Repository;

import gaun.apply.entity.form.IpMacFormData;

@Repository
public interface IpMacFormRepository extends BaseFormRepository<IpMacFormData> {
    IpMacFormData findByTcKimlikNo(String tcKimlikNo);
    Optional<IpMacFormData> findById(Long id);
    List<IpMacFormData> findByApplyDateAfter(LocalDateTime date);
    List<IpMacFormData> findTop100ByOrderByApplyDateDesc();
}