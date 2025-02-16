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
    
    @Column(name = "ftp_username")
    private String ftpUsername;
    
    @Column(name = "mysql_username")
    private String mysqlUsername;
    
    @Column(name = "purpose", columnDefinition = "TEXT")
    private String purpose;
    
    @Column(name = "hosting_type")
    private String hostingType; // Shared veya Dedicated
} 