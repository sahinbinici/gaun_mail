package gaun.apply.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import gaun.apply.entity.EduroamFormData;

public interface EduroamFormRepository extends JpaRepository<EduroamFormData, Long> {
    long countByStatus(boolean status);
    List<EduroamFormData> findTop10ByOrderByApplyDateDesc();

    EduroamFormData findByUsername(String username);
}