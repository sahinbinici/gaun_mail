package gaun.apply.domain.user.service;

import java.util.List;

import gaun.apply.application.dto.EduroamFormDto;
import gaun.apply.application.dto.MailFormDto;
import gaun.apply.application.dto.StudentDto;
import gaun.apply.application.dto.UserDto;
import gaun.apply.domain.user.entity.User;

public interface UserService {
    void saveUserStudent(StudentDto studentDto);
    void saveUserStaff(UserDto userDto);

    User findByidentityNumber(String identityNumber);

    User findByTcKimlikNo(String tcKimlikNo);

    void updatePassword(String tcKimlikNo, String newPassword);

    List<UserDto> findAllUsers();
    
    List<User> findAllUsersEntity();
    
    List<User> findRecentUsers(int days);
    
    List<User> findTop50Users();
    
    List<User> searchUsers(String query);
    
    User findById(Long id);
    
    void saveUser(User user);
    
    void deleteUser(Long id);
/*
    void saveMailApply(MailFormDto mailFormDto);

    void saveEduroamApply(EduroamFormDto eduroamFormDto);
    */
}
