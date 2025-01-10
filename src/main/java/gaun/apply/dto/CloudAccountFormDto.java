package gaun.apply.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class CloudAccountFormDto {
    private String tcKimlikNo;
    
    @NotEmpty(message = "Kullanım amacı boş olamaz")
    private String purpose;
    
    @NotEmpty(message = "Kapasite seçilmelidir")
    private String capacity;
} 