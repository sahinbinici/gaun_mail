package gaun.apply.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "student")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private boolean status;
    private String error;
    private String ogrenciNo;
    private String password;
    private String ad;
    private String soyad;
    private String egitimDerecesi;
    private String fakKod;
    private String bolumAd;
    private String programAd;
    private String eposta1;
    private String eposta2;
    private String sinif;
    private String durumu;
    private String ayrilisTarihi;
    private String tcKimlikNo;

}
