package gaun.apply.dto;


import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long id;
    @NotEmpty
    private String ad;
    @NotEmpty
    private String soyad;
    @NotEmpty(message = "IdentityNumber should not be empty")
    private String ogrenciNo;
    @NotEmpty(message = "Password should not be empty")
    private String password;
}