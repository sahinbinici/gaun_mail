package gaun.apply.entity.form;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "vpn_forms")
public class VpnFormData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String tcKimlikNo;
    private String purpose;
    private String ipAddress;
    private LocalDateTime applyDate;
    private boolean status;
} 