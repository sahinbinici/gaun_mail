package gaun.apply.entity.form;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "web_academic_form")
public class WebAcademicFormData extends BaseFormData {
    @Column(name = "domain_name")
    private String domainName;
    
    @Column(name = "purpose")
    private String purpose;
    
    @Column(name = "hosting_type")
    private String hostingType; // Shared veya Dedicated
} 