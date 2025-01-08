package gaun.apply.dto;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class StaffDto {

    private String tcKimlikNo;
    private String ad;
    private String soyad;
    private String calistigiBirim;
    private String unvan;
}
