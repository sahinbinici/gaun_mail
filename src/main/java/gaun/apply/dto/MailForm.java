package gaun.apply.dto;

import lombok.Data;

@Data
public class MailForm {
    private String username;
    private String email;
    private String password;
    private String confirmPassword;
} 