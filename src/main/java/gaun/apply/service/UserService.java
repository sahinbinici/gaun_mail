package gaun.apply.service;

import gaun.apply.dto.StudentDto;
import gaun.apply.dto.UserDto;
import gaun.apply.entity.User;

import java.util.List;

public interface UserService {
    void saveUser(StudentDto studentDto);

    User findByidentityNumber(String identityNumber);

    List<UserDto> findAllUsers();
}