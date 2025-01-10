package gaun.apply.entity.form;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "eduroam_forms")
public class EduroamFormData extends BaseFormData {
    private String username;
    private String password;
} 