package gaun.apply.entity.form;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "cloud_account_forms")
public class CloudAccountFormData extends BaseFormData {
    private String purpose;
    private String capacity;
} 