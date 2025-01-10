package gaun.apply.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class VpnFormDto {
    private String tcKimlikNo;
    
    @NotEmpty(message = "Kullanım amacı boş olamaz")
    private String purpose;
    
    @NotEmpty(message = "IP adresi boş olamaz")
    @Pattern(regexp = "^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$", message = "Geçersiz IP adresi formatı")
    private String ipAddress;
} 