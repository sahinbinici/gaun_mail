package gaun.apply.domain.eduroam.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import gaun.apply.domain.common.BaseFormData;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@Getter
@Setter
@Entity
@Table(name = "eduroam_forms")
public class EduroamFormData extends BaseFormData {
    private String ogrenciNo;
    private String sicilNo;
    private String password;
    private String ad;
    private String soyad;
    private String fakulte;
    private String bolum;
    private String gsm1;
    private String email;
    private Date dogumTarihi;
    private String fakkod;
    private String gsm;
    private String calistigiBirim;
    private String unvan;
    
    // Getter methods that are used in AdminController
    public String getAd() {
        return ad;
    }
    
    public String getSoyad() {
        return soyad;
    }
    
    public String getFakulte() {
        return fakulte;
    }
    
    public String getBolum() {
        return bolum;
    }
    
    public String getGsm1() {
        return gsm1;
    }
    
    public String getEmail() {
        return email;
    }
}
