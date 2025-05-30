package gaun.apply.domain.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "student")
public class Student {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "status")
    private boolean status;
    @Column(name = "error")
    private String error;
    @Column(name = "ogrenci_no")
    private String ogrenciNo;
    @Column(name = "ad")
    private String ad;
    @Column(name = "soyad")
    private String soyad;
    @Column(name = "egitim_derecesi")
    private String egitimDerecesi;
    @Column(name = "fakulte_ad")
    private String fakKod;
    @Column(name = "bolum_ad")
    private String bolumAd;
    @Column(name = "program_ad")
    private String programAd;
    @Column(name = "eposta1")
    private String eposta1;
    @Column(name = "eposta2")
    private String eposta2;
    @Column(name = "sinif")
    private String sinif;
    @Column(name = "durumu")
    private String durumu;
    @Column(name = "ayrilis_tarihi")
    private String ayrilisTarihi;
    @Column(name = "TC_KIMLIK_NO")
    private String tcKimlikNo;
    @Column(name = "gsm1")
    private String gsm1;
    @Column(name = "dogum_tarihi")
    private String dogumTarihi;
}
