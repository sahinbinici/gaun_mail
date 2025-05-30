package gaun.apply.domain.user.service;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import gaun.apply.application.dto.StudentDto;
import gaun.apply.domain.user.entity.Student;
import gaun.apply.domain.user.repository.StudentRepository;
import gaun.apply.common.util.ConvertUtil;

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

    public void saveStudent(StudentDto studentDto) {
        Student student = modelMapper.map(studentDto, Student.class);
        studentRepository.save(student);
    }

    public StudentDto findByOgrenciNo(String ogrenciNo) {
        Student student = studentRepository.findByOgrenciNo(ogrenciNo);
        return student != null ? modelMapper.map(student, StudentDto.class) : null;
    }

    public String createEmailAddress(String ogrenciNo) {
        Student student=studentRepository.findByOgrenciNo(ogrenciNo);
        String adIlkHarf=student.getAd().substring(0,1);
        String soyadIlkHarf=student.getSoyad().substring(0,1);
        String numaraSon6=student.getOgrenciNo().substring(student.getOgrenciNo().length()-6);
        return (adIlkHarf+soyadIlkHarf+numaraSon6).toLowerCase().replace("ı","i").replace("ö","o").replace("ü","u").replace("ğ","g");
    }
}
