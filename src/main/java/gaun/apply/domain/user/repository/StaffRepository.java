package gaun.apply.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gaun.apply.domain.user.entity.Staff;
import gaun.apply.application.dto.StaffDto;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {
    Staff findByTcKimlikNo(String tcKimlikNo);
}
