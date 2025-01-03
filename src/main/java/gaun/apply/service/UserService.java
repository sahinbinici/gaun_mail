package gaun.apply.service;

import java.util.List;

import gaun.apply.dto.MailFormDto;
import gaun.apply.dto.StudentDto;
import gaun.apply.dto.UserDto;
import gaun.apply.entity.user.User;

public interface UserService {
    void saveUserStudent(StudentDto studentDto);
    void saveUserStaff(UserDto userDto);

    User findByidentityNumber(String identityNumber);

    List<UserDto> findAllUsers();

    void saveMailApply(MailFormDto mailFormDto);

    long countActiveUsers();

    long countUsers();
}