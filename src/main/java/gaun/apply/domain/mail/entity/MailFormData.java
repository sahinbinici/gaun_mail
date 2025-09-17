package gaun.apply.domain.mail.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import gaun.apply.common.enums.ApplicationStatusEnum;
import gaun.apply.domain.common.BaseFormData;

import java.sql.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "mail_forms")
public class MailFormData extends BaseFormData {
    @Column(name = "sicil")
    private Integer sicil;
    @Column(name = "ogrenci_no")
    private String ogrenciNo;
    @Column(name = "fakulte")
    private String fakulteKod;
    @Column(name = "email")
    private String email;
    @Column(name="mail_kullaniciAdi")
    private String mailKullaniciAdi;
    @Column(name = "password")
    private String password;
    @Column(name = "ad")
    private String ad;
    @Column(name = "soyad")
    private String soyad;
    @Column(name = "dogum_tarihi")
    private Date dogumTarihi;
    @Column(name = "fakkod")
    private String fakkod;
    @Column(name = "bolum")
    private String bolum;
    @Column(name = "gsm")
    private String gsm;
    @Column(name = "calistigi_birim")
    private String calistigiBirim;
    @Column(name = "unvan")
    private String unvan;
    
    @Enumerated(EnumType.STRING)
    private ApplicationStatusEnum applicationStatus = ApplicationStatusEnum.PENDING;
    
    // Getter methods that are used in AdminController
    public String getAd() {
        return ad;
    }
    
    public String getSoyad() {
        return soyad;
    }
    
    public String getFakulte() {
        return fakkod;
    }
    
    public String getBolum() {
        return bolum;
    }
    
    public String getGsm1() {
        return gsm;
    }
}
