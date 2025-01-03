package gaun.apply.entity.form;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "vpn_form")
public class VpnFormData extends BaseFormData {
    @Column(name = "purpose")
    private String purpose;
    
    @Column(name = "duration")
    private Integer duration; // Ay cinsinden
} 