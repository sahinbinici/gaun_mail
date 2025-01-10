package gaun.apply.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class WirelessNetworkFormDto {
    private String tcKimlikNo;
    
    @NotEmpty(message = "MAC adresi boş olamaz")
    @Pattern(regexp = "^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$", message = "Geçersiz MAC adresi formatı")
    private String macAddress;
    
    @NotEmpty(message = "Cihaz türü seçilmelidir")
    private String deviceType;
} 