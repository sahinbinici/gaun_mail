package gaun.apply.service;

import gaun.apply.dto.MailFormDto;
import gaun.apply.dto.StudentDto;
import gaun.apply.dto.UserDto;
import gaun.apply.entity.Student;
import gaun.apply.entity.User;

import java.util.List;

public interface UserService {
    void saveUserStudent(StudentDto studentDto);
    void saveUserStaff(UserDto userDto);

    User findByidentityNumber(String identityNumber);

    List<UserDto> findAllUsers();

    void saveMailApply(MailFormDto mailFormDto);

}