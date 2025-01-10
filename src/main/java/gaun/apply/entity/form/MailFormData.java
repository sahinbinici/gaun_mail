package gaun.apply.entity.form;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "mail_forms")
public class MailFormData extends BaseFormData {
    private String username;
    private String email;
    private String password;
} 