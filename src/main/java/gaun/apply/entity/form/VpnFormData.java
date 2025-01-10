package gaun.apply.entity.form;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "vpn_forms")
public class VpnFormData extends BaseFormData {
    private String purpose;
    private String ipAddress;
} 