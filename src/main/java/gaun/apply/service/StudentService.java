package gaun.apply.service;

import gaun.apply.dto.StudentDto;
import gaun.apply.entity.Role;
import gaun.apply.entity.Student;
import gaun.apply.entity.User;
import gaun.apply.repository.StudentRepository;
import gaun.apply.util.ConvertUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import static gaun.apply.util.ConvertUtil.getStudentFromObs;

@Service
public class StudentService {
    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public void saveStudent(StudentDto studentDto) throws NoSuchAlgorithmException {
        StudentDto studentDtoData=ConvertUtil.getStudentFromObs(studentDto);
        Student student = new Student();
        student.setAd(studentDtoData.getAd());
        student.setSoyad(studentDtoData.getSoyad());
        student.setOgrenciNo(studentDtoData.getOgrenciNo());
        student.setBolumAd(studentDtoData.getBolumAd());
        student.setDurumu(studentDtoData.getDurumu());
        student.setEgitimDerecesi(studentDtoData.getEgitimDerecesi());
        student.setFakKod(studentDtoData.getFakKod());
        student.setProgramAd(studentDtoData.getProgramAd());
        student.setSinif(studentDtoData.getSinif());
        student.setTcKimlikNo(studentDtoData.getTcKimlikNo());
        student.setEposta1(studentDtoData.getEposta1());
        student.setEposta2(studentDtoData.getEposta2());
        student.setAyrilisTarihi(studentDtoData.getAyrilisTarihi());

        studentRepository.save(student);
    }

}
