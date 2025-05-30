package gaun.apply.domain.eduroam.repository;

import java.util.Optional;
import java.util.List;
import java.time.LocalDateTime;

import org.springframework.stereotype.Repository;

import gaun.apply.domain.eduroam.entity.EduroamFormData;
import gaun.apply.domain.common.BaseFormRepository;

@Repository
public interface EduroamFormRepository extends BaseFormRepository<EduroamFormData> {
    EduroamFormData findByUsername(String username);
    EduroamFormData findByTcKimlikNo(String tcKimlikNo);
    List<EduroamFormData> findTop100ByOrderByApplyDateDesc();
    List<EduroamFormData> findByApplyDateAfter(LocalDateTime date);
    Optional<EduroamFormData> findById(Long id);
}
