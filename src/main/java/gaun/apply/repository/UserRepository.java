package gaun.apply.repository;

import gaun.apply.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByIdentityNumber(String identityNumber);
}