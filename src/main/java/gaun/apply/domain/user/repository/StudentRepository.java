package gaun.apply.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gaun.apply.domain.user.entity.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student,Long> {
    Student findByOgrenciNo(String ogrenciNo);
    Student findByTcKimlikNo(String tcKimlikNo);
}
