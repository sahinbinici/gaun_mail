package gaun.apply.application.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetDto {

    @NotEmpty(message = "Şifre boş olamaz")
    private String password;

    @NotEmpty(message = "Şifre tekrarı boş olamaz")
    private String confirmPassword;
}
