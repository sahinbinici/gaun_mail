package gaun.apply.entity.form;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "cloud_account_form")
public class CloudAccountFormData extends BaseFormData {
    @Column(name = "storage_size")
    private Integer storageSize;
    
    @Column(name = "purpose")
    private String purpose;
} 