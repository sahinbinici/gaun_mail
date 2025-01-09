package gaun.apply.dto;

import lombok.Data;
import jakarta.validation.constraints.NotEmpty;

@Data
public class WirelessNetworkFormDto {
    @NotEmpty(message = "Kullanıcı adı boş olamaz")
    private String username;
    
    @NotEmpty(message = "Şifre boş olamaz")
    private String password;
    
    private boolean status;
} 