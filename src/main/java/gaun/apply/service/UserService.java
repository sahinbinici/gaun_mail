package gaun.apply.service;

import gaun.apply.dto.UserDto;
import gaun.apply.entity.User;

import java.util.List;

public interface UserService {
    void saveUser(UserDto userDto);

    User findByidentityNumber(String identityNumber);

    List<UserDto> findAllUsers();
}