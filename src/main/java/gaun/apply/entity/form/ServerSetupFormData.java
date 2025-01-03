package gaun.apply.entity.form;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "server_setup_form")
public class ServerSetupFormData extends BaseFormData {
    @Column(name = "server_type")
    private String serverType;
    
    @Column(name = "purpose")
    private String purpose;
    
    @Column(name = "specifications")
    private String specifications;
} 