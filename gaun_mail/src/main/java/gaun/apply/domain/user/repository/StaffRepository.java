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

    @Query(value = """
            SELECT p.tcKiml AS tcKimlikNo,
            p.psicno AS sicilNo,
            p.peradi AS ad,
            p.soyadi AS soyad,
            b.BRKK30 AS calistigiBirim,
            u.unvack AS unvan,
            NULL AS gsm,
            p.dogumTarihi AS dogumTarihi
        FROM person p
        JOIN brkodu b ON p.brkodu = b.BRKODU
        JOIN unvkod u ON p.unvkod = u.unvkod
        WHERE p.tcKiml = :tcKimlikNo
        """, nativeQuery = true)
    Object findStaffByTcKimlikNo(@Param("tcKimlikNo") String tcKimlikNo);

}
