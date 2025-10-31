package gaun.apply.domain.user.repository;

import gaun.apply.domain.user.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CalyerKurumdisiRepository extends JpaRepository<Staff, Long> {

    @Query(value = """
        SELECT c.tckiml AS tcKimlikNo,
               NULL AS sicilNo,
               c.peradi AS ad,
               c.soyadi AS soyad,
               b.BRKK30 AS calistigiBirim,
               u.unvack AS unvan,
               NULL AS gsm,
               NULL AS dogumTarihi
        FROM calyerkurumdisi c
        LEFT JOIN brkodu b ON c.brkodu2 = b.BRKODU
        LEFT JOIN unvkod u ON c.unvkod = u.unvkod
        WHERE c.tckiml = :tcKimlikNo
        """, nativeQuery = true)
    Object findStaffByTcKimlikNo(@Param("tcKimlikNo") String tcKimlikNo);
}
