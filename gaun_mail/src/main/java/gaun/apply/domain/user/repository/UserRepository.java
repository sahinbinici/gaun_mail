package gaun.apply.domain.user.repository;

import gaun.apply.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByIdentityNumber(String identityNumber);
    long countByActive(boolean active);
    User findByTcKimlikNo(String tcKimlikNo);
    boolean existsByIdentityNumber(String identityNumber);
    boolean existsByTcKimlikNo(String tcKimlikNo);
    boolean existsByIdentityNumberOrTcKimlikNo(String identityNumber, String tcKimlikNo);
    List<User> findByRegisterDateAfterOrderByRegisterDateDesc(LocalDate date);
    List<User> findTop50ByOrderByRegisterDateDesc();
    
    @Query("SELECT u FROM User u WHERE " +
           "LOWER(u.identityNumber) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(u.tcKimlikNo) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(u.ad) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(u.soyad) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "ORDER BY u.registerDate DESC")
    List<User> searchUsers(@Param("query") String query);
}
