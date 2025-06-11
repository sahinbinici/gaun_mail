package gaun.apply.domain.user.repository;

import gaun.apply.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByIdentityNumber(String identityNumber);
    long countByActive(boolean active);
    User findByTcKimlikNo(String tcKimlikNo);
}
