package gaun.apply.entity.form;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "wireless_network_form")
public class WirelessNetworkFormData extends BaseFormData {
    @Column(name = "mac_address")
    private String macAddress;
    
    @Column(name = "device_type")
    private String deviceType;
} 