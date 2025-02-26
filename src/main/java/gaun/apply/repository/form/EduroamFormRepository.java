package gaun.apply.repository.form;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gaun.apply.entity.form.EduroamFormData;

@Repository
public interface EduroamFormRepository extends JpaRepository<EduroamFormData, Long> {
    EduroamFormData findByUsername(String username);

    Optional<EduroamFormData> findById(Long id);

    List<EduroamFormData> findTop10ByOrderByApplyDateDesc();

    void deleteById(Long id);
} 