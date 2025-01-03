package gaun.apply.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class MailFormDto {
    private String username;
    private String email;
    private String password;
    private String confirmPassword;
    private LocalDate applyDate;
    private boolean status=false;
} 