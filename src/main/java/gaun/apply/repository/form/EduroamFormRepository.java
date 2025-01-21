package gaun.apply.repository.form;

import org.springframework.stereotype.Repository;

import gaun.apply.entity.form.EduroamFormData;

@Repository
public interface EduroamFormRepository extends BaseFormRepository<EduroamFormData> {
    EduroamFormData findByUsername(String username);
} 