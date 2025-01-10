package gaun.apply.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class IpMacFormDto {
    private String tcKimlikNo;
    
    @NotEmpty(message = "MAC adresi boş olamaz")
    @Pattern(regexp = "^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$", message = "Geçersiz MAC adresi formatı")
    private String macAddress;
    
    @NotEmpty(message = "IP adresi boş olamaz")
    @Pattern(regexp = "^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$", message = "Geçersiz IP adresi formatı")
    private String ipAddress;
    
    @NotEmpty(message = "Lokasyon boş olamaz")
    private String location;
} 