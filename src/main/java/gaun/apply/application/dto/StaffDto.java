package gaun.apply.application.dto;

import lombok.Data;

import java.sql.Date;

@Data
public class StaffDto {

    private Long tcKimlikNo;
    private Integer sicilNo;
    private String ad;
    private String soyad;
    private String calistigiBirim;
    private String unvan;
    private String gsm;
    private String email;
    private Date dogumTarihi;
}
