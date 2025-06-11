package gaun.apply.application.dto;

import lombok.Data;

@Data
public class MailFormDto {
    private String ogrenciNo;
    private String tcKimlikNo;
    private String ad;
    private String soyad;
    private String fakulteAd;
    private String bolumAd;
    private String email;
    private String password;
    private String confirmPassword;
    private boolean status;
}
