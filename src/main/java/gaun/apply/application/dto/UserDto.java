package gaun.apply.application.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Date;
import java.time.LocalDate;

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
