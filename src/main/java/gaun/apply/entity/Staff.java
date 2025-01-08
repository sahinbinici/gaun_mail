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
    
    @Column(name = "ad")
    private String ad;
    
    @Column(name = "soyad")
    private String soyad;
    
    @Column(name = "calistigiBirim")
    private String calistigiBirim;
    
    @Column(name = "unvan")
    private String unvan;
}
