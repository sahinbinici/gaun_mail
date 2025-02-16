package gaun.apply.service.form;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import gaun.apply.entity.form.EduroamFormData;
import gaun.apply.repository.form.EduroamFormRepository;

@Service
public class EduroamFormService {
    private final EduroamFormRepository eduroamFormRepository;

    public EduroamFormService(EduroamFormRepository eduroamFormRepository) {
        this.eduroamFormRepository = eduroamFormRepository;
    }

    public EduroamFormData eduroamFormData(String username){
        return eduroamFormRepository.findByUsername(username);
    }

    public long countByStatus(boolean status){
        return eduroamFormRepository.countByStatus(status);
    }
    public List<EduroamFormData> findTop10ByOrderByApplyDateDesc(){
        return eduroamFormRepository.findTop10ByOrderByApplyDateDesc();
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
}
