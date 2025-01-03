package gaun.apply.service;

import java.security.NoSuchAlgorithmException;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import gaun.apply.dto.StudentDto;
import gaun.apply.entity.Student;
import gaun.apply.repository.StudentRepository;
import gaun.apply.util.ConvertUtil;

@Service
public class StudentService {
    private final StudentRepository studentRepository;
    private final ModelMapper modelMapper;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
        this.modelMapper = new ModelMapper();
        
        modelMapper.getConfiguration()
            .setFieldMatchingEnabled(true)
            .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE);
    }

    public void saveStudent(StudentDto studentDto) throws NoSuchAlgorithmException {
        StudentDto studentDtoData = ConvertUtil.getStudentFromObs(studentDto);
        Student student = modelMapper.map(studentDtoData, Student.class);
        studentRepository.save(student);
    }

    public StudentDto findByOgrenciNo(String ogrenciNo) {
        Student student = studentRepository.findByOgrenciNo(ogrenciNo);
        return student != null ? modelMapper.map(student, StudentDto.class) : null;
    }
}
