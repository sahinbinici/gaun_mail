package gaun.apply.entity.form;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.EqualsAndHashCode;
import gaun.apply.enums.ApplicationStatusEnum;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "mail_forms")
public class MailFormData extends BaseFormData {
    private String username;
    private String email;
    private String password;
    
    @Enumerated(EnumType.STRING)
    private ApplicationStatusEnum applicationStatus = ApplicationStatusEnum.PENDING;
}