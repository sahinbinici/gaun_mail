package gaun.apply.dto;

import lombok.Data;

@Data
public class MailFormDto {
    private String username;
    private String tcKimlikNo;
    private String email;
    private String password;
    private String confirmPassword;
    private boolean status;
} 