package gaun.apply.service.form;

import org.springframework.stereotype.Service;
import gaun.apply.entity.form.BaseFormData;
import gaun.apply.repository.form.BaseFormRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.jpa.repository.JpaRepository;

@Service
public class FormService {
    private final Map<String, BaseFormRepository<? extends BaseFormData>> repositories;
    
    public FormService(List<BaseFormRepository<? extends BaseFormData>> repos) {
        repositories = repos.stream()
            .collect(Collectors.toMap(
                r -> r.getClass().getInterfaces()[0].getSimpleName(),
                r -> r
            ));
    }
    
    public void activateForm(Long id, Class<? extends BaseFormData> formClass) {
        String repoName = formClass.getSimpleName().replace("Data", "Repository");
        @SuppressWarnings("unchecked")
        BaseFormRepository<BaseFormData> repository = 
            (BaseFormRepository<BaseFormData>) repositories.get(repoName);
        
        if (repository == null) {
            throw new RuntimeException("Repository bulunamadı: " + repoName);
        }
        
        BaseFormData form = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Form bulunamadı"));
        form.setStatus(true);
        repository.save(form);
    }
} 