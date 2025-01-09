package gaun.apply.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EduroamFormDto {

    @NotEmpty(message = "Kullanıcı adı boş olamaz")
    private String username;
    
    @NotEmpty(message = "Şifre boş olamaz")
    @Size(min = 8, message = "Şifre en az 8 karakter olmalıdır")
    private String password;

    private String confirmPassword;
    
    private boolean status;
}
