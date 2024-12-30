package gaun.apply.service;

import gaun.apply.dto.StudentDto;
import gaun.apply.dto.UserDto;
import gaun.apply.entity.Role;
import gaun.apply.entity.User;
import gaun.apply.repository.RoleRepository;
import gaun.apply.repository.UserRepository;
import gaun.apply.util.ConvertUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static gaun.apply.util.ConvertUtil.convertEntityToDto;

@Service
public class UserServiceImpl implements UserService {
    @Value("${base.url}")
    private String baseUrl;

    private final RestTemplate restTemplate;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;

    public UserServiceImpl(RestTemplate restTemplate, UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.restTemplate = restTemplate;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void saveUser(StudentDto studentDto) {

        try {
            String pass = ConvertUtil.convertPasswordToMD5(studentDto.getPassword());
            String url = baseUrl + "?check=gaun_mobil&u=" + studentDto.getOgrenciNo() + "&p=" + pass;
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                StudentDto studentFromService = ConvertUtil.convertJsonToStudentDto(response.getBody());
                    User user = new User();
                    user.setName(studentFromService.getAd());
                    user.setLastname(studentFromService.getSoyad());
                    user.setIdentityNumber(studentFromService.getOgrenciNo());
                    //encrypt the password using spring security
                    user.setPassword(passwordEncoder.encode(studentDto.getPassword()));
                    List<Role> roles = new ArrayList<>();
                    roles.add(new Role("ROLE_USER"));
                    user.setRoles(roles);
                    userRepository.save(user);

            } else {
                throw new UsernameNotFoundException("Kullanıcı bulunamadı");
            }
        } catch (Exception e) {
            throw new UsernameNotFoundException("Servis hatası: " + e.getMessage());
        }
        /*
        User user = new User();
        user.setName(studentDto.getFirstName());
        user.setLastname(studentDto.getLastName());
        user.setIdentityNumber(studentDto.getIdentityNumber());
        //encrypt the password using spring security
        user.setPassword(passwordEncoder.encode(studentDto.getPassword()));

        Role role = roleRepository.findByName("ROLE_ADMIN");
        if (role == null) {
            role = checkRoleExist();
        }
        user.setRoles(List.of(role));
        userRepository.save(user); */
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

}
