package gaun.apply.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import gaun.apply.entity.Student;

public interface StudentRepository extends JpaRepository<Student,Long> {
    Student findByOgrenciNo(String ogrenciNo);
}
