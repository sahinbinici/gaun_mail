package gaun.apply.repository;

import gaun.apply.dto.StaffDto;
import org.springframework.data.jpa.repository.JpaRepository;

import gaun.apply.entity.Staff;

public interface StaffRepository extends JpaRepository<Staff, Long> {
    Staff findByTcKimlikNo(String tcKimlikNo);
} 