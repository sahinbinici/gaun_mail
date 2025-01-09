package gaun.apply.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import gaun.apply.entity.EduroamFormData;

public interface EduroamFormRepository extends JpaRepository<EduroamFormData, Long> {
    EduroamFormData findByUsername(String username);
    
    // Duruma göre başvuru sayısını sayan metod
    long countByStatus(boolean status);
    
    // Son 10 başvuruyu getiren metod
    List<EduroamFormData> findTop10ByOrderByApplyDateDesc();
}