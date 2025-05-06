package gaun.apply.repository.form;

import java.util.Optional;
import java.util.List;
import java.time.LocalDateTime;

import org.springframework.stereotype.Repository;

import gaun.apply.entity.form.VpnFormData;

@Repository
public interface VpnFormRepository extends BaseFormRepository<VpnFormData> {
    VpnFormData findByTcKimlikNo(String tcKimlikNo);
    Optional<VpnFormData> findById(Long id);
    List<VpnFormData> findByApplyDateAfter(LocalDateTime date);
    List<VpnFormData> findTop100ByOrderByApplyDateDesc();
}