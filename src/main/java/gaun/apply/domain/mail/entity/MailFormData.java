package gaun.apply.domain.mail.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
    private Integer sicil;
    private String ogrenciNo;
    private String email;
    private String password;
    private String ad;
    private String soyad;
    private Date dogumTarihi;
    private String fakkod;
    private String bolum;
    private String gsm;
    private String calistigiBirim;
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
