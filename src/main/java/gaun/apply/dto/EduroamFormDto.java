package gaun.apply.dto;

import java.time.LocalDate;

import lombok.Data;
@Data
public class EduroamFormDto {

    private String username;
    private String password;
    private String confirmPassword;
    private LocalDate applyDate;
    private boolean status;
}
