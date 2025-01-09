package gaun.apply.service.form;

import org.springframework.stereotype.Service;
import gaun.apply.entity.EduroamFormData;
import gaun.apply.repository.EduroamFormRepository;

@Service
public class FormService {
    private final EduroamFormRepository eduroamFormRepository;

    public FormService(EduroamFormRepository eduroamFormRepository) {
        this.eduroamFormRepository = eduroamFormRepository;
    }

    public EduroamFormData saveEduroamForm(EduroamFormData formData) {
        return eduroamFormRepository.save(formData);
    }
} 