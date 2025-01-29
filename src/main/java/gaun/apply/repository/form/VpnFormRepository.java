package gaun.apply.repository.form;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import gaun.apply.entity.form.VpnFormData;
import org.springframework.stereotype.Repository;

@Repository
public interface VpnFormRepository extends BaseFormRepository<VpnFormData> {
    VpnFormData findByTcKimlikNo(String tcKimlikNo);
} 