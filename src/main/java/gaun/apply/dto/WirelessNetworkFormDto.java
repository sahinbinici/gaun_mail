package gaun.apply.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class WirelessNetworkFormDto {
    @NotEmpty(message = "MAC adresi boş olamaz")
    private String macAddress;
    
    @NotEmpty(message = "Cihaz tipi boş olamaz")
    private String deviceType;
} 