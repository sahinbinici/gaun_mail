package gaun.apply.domain.eduroam.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import gaun.apply.domain.common.BaseFormData;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "eduroam_forms")
public class EduroamFormData extends BaseFormData {
    private String username;
    private String password;
    private String ad;
    private String soyad;
    private String fakulte;
    private String bolum;
    private String gsm1;
    private String email;
    
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
