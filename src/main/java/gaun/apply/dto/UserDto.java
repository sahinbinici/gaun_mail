package gaun.apply.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class UserDto {
    @NotEmpty(message = "TC Kimlik No boş olamaz")
    private String tcKimlikNo;
    
    @NotEmpty(message = "Şifre boş olamaz")
    private String password;
}