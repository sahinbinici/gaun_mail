package gaun.apply.infrastructure.repository;

import gaun.apply.infrastructure.entity.AdminTabPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminTabPermissionRepository extends JpaRepository<AdminTabPermission, Long> {
    List<AdminTabPermission> findByUserId(Long userId);
    boolean existsByUserIdAndTabNameAndHasAccessTrue(Long userId, String tabName);
}
