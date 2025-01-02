package gaun.apply.dto;

import lombok.Data;

@Data
public class MailFormDto {
    private String username;
    private String email;
    private String password;
    private String confirmPassword;
    private String applyDate;
    private boolean status=false;
} 