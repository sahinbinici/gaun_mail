package gaun.apply.domain.eduroam.entity;

import jakarta.persistence.Column;
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
    @Column(name = "ogrenci_no")
    private String ogrenciNo;
    @Column(name = "sicil_no")
    private String sicilNo;
    @Column(name = "password")
    private String password;
    @Column(name = "ad")
    private String ad;
    @Column(name = "soyad")
    private String soyad;
    @Column(name = "fakulte")
    private String fakulte;
    @Column(name = "bolum")
    private String bolum;
    @Column(name = "gsm1")
    private String gsm1;
    @Column(name = "email")
    private String email;
    @Column(name = "dogum_tarihi")
    private Date dogumTarihi;
    @Column(name = "fakkod")
    private String fakkod;
    @Column(name = "gsm")
    private String gsm;
    @Column(name = "calistigi_birim")
    private String calistigiBirim;
    @Column(name = "unvan")
    private String unvan;
}
