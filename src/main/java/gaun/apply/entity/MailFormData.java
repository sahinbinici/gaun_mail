package gaun.apply.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "mail_form")
@Data
public class MailFormData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "username")
    private String username;
    @Column(name = "email")
    private String email;
    @Column(name = "password")
    private String password;
    @Column(name = "confirm_password")
    private String confirmPassword;
    @Column(name = "status")
    private boolean status=false;
}
