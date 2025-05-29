package gaun.apply.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "staff")
public class Staff {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "tc_kimlik_no")
    private String tcKimlikNo;

    @Column(name = "sicil_no")
    private String sicilNo;
    
    @Column(name = "ad")
    private String ad;
    
    @Column(name = "soyad")
    private String soyad;
    
    @Column(name = "calistigiBirim")
    private String calistigiBirim;
    
    @Column(name = "unvan")
    private String unvan;

    @Column(name = "gsm")
    private String gsm;

    @Column(name = "email")
    private String email;

    @Column(name = "dogum_tarihi")
    private String dogumTarihi;
    
    @Column(name = "isAdmin")
    private boolean isAdmin;
}
