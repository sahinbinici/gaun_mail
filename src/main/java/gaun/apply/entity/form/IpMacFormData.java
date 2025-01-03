package gaun.apply.entity.form;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "ip_mac_form")
public class IpMacFormData extends BaseFormData {
    @Column(name = "ip_address")
    private String ipAddress;
    
    @Column(name = "mac_address")
    private String macAddress;
    
    @Column(name = "location")
    private String location;
} 