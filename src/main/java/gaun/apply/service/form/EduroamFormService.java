package gaun.apply.service.form;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import gaun.apply.entity.form.EduroamFormData;
import gaun.apply.repository.form.EduroamFormRepository;
import gaun.apply.enums.ApplicationStatusEnum;

@Service
public class EduroamFormService {
    private final EduroamFormRepository eduroamFormRepository;

    public EduroamFormService(EduroamFormRepository eduroamFormRepository) {
        this.eduroamFormRepository = eduroamFormRepository;
    }

    public EduroamFormData eduroamFormData(String username){
        return eduroamFormRepository.findByUsername(username);
    }
    
    public EduroamFormData findByTcKimlikNo(String tcKimlikNo) {
        return eduroamFormRepository.findByTcKimlikNo(tcKimlikNo);
    }

    public List<EduroamFormData> findTop10ByOrderByApplyDateDesc(){
        return eduroamFormRepository.findTop10ByOrderByApplyDateDesc();
    }
    
    public List<EduroamFormData> findLast100Applications() {
        return eduroamFormRepository.findTop100ByOrderByApplyDateDesc();
    }
    
    public List<EduroamFormData> findLastMonthApplications() {
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        return eduroamFormRepository.findByApplyDateAfter(oneMonthAgo);
    }

    public void saveEduroamFormData(EduroamFormData eduroamFormData){
        eduroamFormRepository.save(eduroamFormData);
    }

    public Optional<EduroamFormData> findById(Long id) {
        return eduroamFormRepository.findById(id);
    }

    public List<EduroamFormData> getAllEduroamForms() {
        return eduroamFormRepository.findAll();
    }

    public void deleteEduroamForm(Long id) {
        eduroamFormRepository.deleteById(id);
    }

    public List<EduroamFormData> findByDurum(String durum) {
        return eduroamFormRepository.findByApplicationStatus(ApplicationStatusEnum.valueOf(durum));
    }
}
