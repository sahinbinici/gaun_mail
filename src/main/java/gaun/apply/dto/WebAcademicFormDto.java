package gaun.apply.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class WebAcademicFormDto {
    private String tcKimlikNo;
    
    @NotEmpty(message = "Domain adı boş olamaz")
    private String domainName;
    
    @NotEmpty(message = "FTP kullanıcı adı boş olamaz")
    private String ftpUsername;
    
    @NotEmpty(message = "MySQL kullanıcı adı boş olamaz")
    private String mysqlUsername;
    
    @NotEmpty(message = "Kullanım amacı boş olamaz")
    private String purpose;
    
    @NotEmpty(message = "Hosting tipi seçilmelidir")
    private String hostingType;
} 