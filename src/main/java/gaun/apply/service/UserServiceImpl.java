package gaun.apply.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import gaun.apply.dto.MailFormDto;
import gaun.apply.dto.StudentDto;
import gaun.apply.dto.UserDto;
import gaun.apply.entity.MailFormData;
import gaun.apply.entity.Role;
import gaun.apply.entity.User;
import gaun.apply.repository.MailFormRepository;
import gaun.apply.repository.RoleRepository;
import gaun.apply.repository.UserRepository;
import static gaun.apply.util.ConvertUtil.convertEntityToDto;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailFormRepository mailFormRepository;

    public UserServiceImpl(UserRepository userRepository,
                          RoleRepository roleRepository,
                          PasswordEncoder passwordEncoder,
                          MailFormRepository mailFormRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailFormRepository = mailFormRepository;
    }

    @Override
    public void saveUserStudent(StudentDto studentDto) {
        User user = new User();
        user.setName(studentDto.getAd());
        user.setLastname(studentDto.getSoyad());
        user.setIdentityNumber(studentDto.getOgrenciNo());
        user.setPassword(passwordEncoder.encode(studentDto.getPassword()));

        // Rol kontrolü ve ataması
        Role role = roleRepository.findByName("ROLE_USER");
        if (role == null) {
            role = new Role("ROLE_USER");
            roleRepository.save(role);
        }
        
        user.setRoles(Arrays.asList(role));
        userRepository.save(user);
    }

    @Override
    public void saveUserStaff(UserDto userDto) {
        User user = new User();
        user.setName(userDto.getAd());
        user.setLastname(userDto.getSoyad());
        user.setIdentityNumber(userDto.getTcKimlikNo());
        //encrypt the password using spring security
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        Role role = roleRepository.findByName("ROLE_ADMIN");
        if (role == null) {
            role = checkRoleExist();
        }
        user.setRoles(List.of(role));
        userRepository.save(user);
    }

    private Role checkRoleExist() {
        Role role = new Role();
        role.setName("ROLE_ADMIN");
        return roleRepository.save(role);
    }

    @Override
    public User findByidentityNumber(String identityNumber) {
        return userRepository.findByIdentityNumber(identityNumber);
    }

    @Override
    public List<UserDto> findAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map((user) -> convertEntityToDto(user))
                .collect(Collectors.toList());
    }

    @Override
    public void saveMailApply(MailFormDto mailFormDto) {
        MailFormData mailFormData = new MailFormData();
        mailFormData.setUsername(mailFormDto.getUsername());
        mailFormData.setEmail(mailFormDto.getEmail());
        mailFormData.setPassword(passwordEncoder.encode(mailFormDto.getPassword()));
        mailFormData.setStatus(mailFormDto.isStatus());
        mailFormRepository.save(mailFormData);
    }


}
