package gaun.apply.entity.form;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "ip_mac_forms")
public class IpMacFormData extends BaseFormData {
    private String macAddress;
    private String ipAddress;
    private String location;
} 