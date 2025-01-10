package gaun.apply.entity.form;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "cloud_account_forms")
public class CloudAccountFormData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String tcKimlikNo;
    private String purpose;
    private String capacity;
    private LocalDateTime applyDate;
    private boolean status;
} 