package gaun.apply.service.form;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import gaun.apply.entity.form.WebAcademicFormData;
import gaun.apply.repository.form.WebAcademicFormRepository;

@Service
public class WebAcademicFormService {
    private final WebAcademicFormRepository webAcademicFormRepository;

    public WebAcademicFormService(WebAcademicFormRepository webAcademicFormRepository) {
        this.webAcademicFormRepository = webAcademicFormRepository;
    }

    public WebAcademicFormData findByTcKimlikNo(String tcKimlikNo) {
        return webAcademicFormRepository.findByTcKimlikNo(tcKimlikNo);
    }

    public long countByStatus(boolean status) {
        return webAcademicFormRepository.countByStatus(status);
    }

    public List<WebAcademicFormData> findTop10ByOrderByApplyDateDesc() {
        return webAcademicFormRepository.findTop10ByOrderByApplyDateDesc();
    }

    public Optional<WebAcademicFormData> findById(long id) {
        return webAcademicFormRepository.findById(id);
    }

    public void saveWebAcademicFormData(WebAcademicFormData webAcademicFormData) {
        webAcademicFormRepository.save(webAcademicFormData);
    }

    public List<WebAcademicFormData> getAllWebAcademicForms() {
        return webAcademicFormRepository.findAll();
    }

    public void deleteWebAcademicForm(Long id) {
        webAcademicFormRepository.deleteById(id);
    }
} 