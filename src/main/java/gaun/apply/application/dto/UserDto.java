package gaun.apply.application.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class UserDto {
    @NotEmpty(message = "TC Kimlik No boş olamaz")
    private String tcKimlikNo;
    
    @NotEmpty(message = "Şifre boş olamaz")
    private String password;
    
    @NotEmpty(message = "Şifre tekrarı boş olamaz")
    private String confirmPassword;
    
    private boolean enabled;
}
