package gaun.apply.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class StudentDto{
    private boolean status;
    private String error;
    @JsonProperty("OGRENCI_NO")
    private String ogrenciNo;
    @NotEmpty(message = "Şifre boş olamaz")
    private String password;
    @NotEmpty(message = "Şifre tekrarı boş olamaz")
    private String confirmPassword;
    @JsonProperty("AD")
    private String ad;
    @JsonProperty("SOYAD")
    private String soyad;
    @JsonProperty("EGITIM_DERECESI")
    private String egitimDerecesi;
    @JsonProperty("FAK_KOD")
    private String fakKod;
    @JsonProperty("BOLUM_AD")
    private String bolumAd;
    @JsonProperty("PROGRAM_AD")
    private String programAd;
    @JsonProperty("EPOSTA1")
    private String eposta1;
    @JsonProperty("EPOSTA2")
    private String eposta2;
    @JsonProperty("SINIF")
    private String sinif;
    @JsonProperty("DURUMU")
    private String durumu;
    @JsonProperty("AYRILIS_TARIHI")
    private String ayrilisTarihi;
    @JsonProperty("TC_KIMLIK_NO")
    private String tcKimlikNo;
    @JsonProperty("GSM1")
    private String gsm1;
    @JsonProperty("DOGUM_TARIHI")
    private String dogumTarihi;
    private String smsCode;
}
