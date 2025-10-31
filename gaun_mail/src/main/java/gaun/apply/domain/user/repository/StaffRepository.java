package gaun.apply.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import gaun.apply.domain.user.entity.Staff;
import gaun.apply.application.dto.StaffDto;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {
    Staff findByTcKimlikNo(String tcKimlikNo);

//@Query(value = "select p.tcKiml, p.psicno, p.peradi, p.soyadi, b.BRKK30, u.unvack, p.ceptel, p.dogumTarihi " +
//        "from person p join brkodu b on p.brkodu=b.BRKODU join unvkod u on p.unvkod=u.unvkod where p.tcKiml= :tcKimlikNo and p.calkod>0", nativeQuery = true)
//StaffDto findStaffDtoByTcKimlikNo(@Param("tcKimlikNo") String tcKimlikNo);

    @Query(value = """
        SELECT p.tcKiml AS tcKimlikNo, 
               p.psicno AS sicilNo, 
               p.peradi AS ad, 
               p.soyadi AS soyad, 
               b.BRKK30 AS calistigiBirim,
               u.unvack AS unvan, 
               t.telefo AS gsm, 
               p.dogumTarihi AS dogumTarihi 
        FROM person p
        JOIN brkodu b ON p.brkodu = b.BRKODU 
        JOIN unvkod u ON p.unvkod = u.unvkod 
        JOIN telefo t ON p.esicno = t.esicno
        WHERE p.tcKiml = :tcKimlikNo 
        AND t.teltur = 'GSM'  
        AND p.calkod > 0
        """, nativeQuery = true)
    Object findStaffByTcKimlikNo(@Param("tcKimlikNo") String tcKimlikNo);


}