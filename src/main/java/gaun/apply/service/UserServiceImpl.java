package gaun.apply.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import gaun.apply.dto.EduroamFormDto;
import gaun.apply.dto.MailFormDto;
import gaun.apply.dto.StudentDto;
import gaun.apply.dto.UserDto;
import gaun.apply.entity.EduroamFormData;
import gaun.apply.entity.MailFormData;
import gaun.apply.entity.Staff;
import gaun.apply.entity.user.Role;
import gaun.apply.entity.user.User;
import gaun.apply.repository.EduroamFormRepository;
import gaun.apply.repository.MailFormRepository;
import gaun.apply.repository.RoleRepository;
import gaun.apply.repository.UserRepository;
import static gaun.apply.util.ConvertUtil.convertEntityToDto;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final StaffService staffService;
    private final MailFormRepository mailFormRepository;
    private final EduroamFormRepository eduroamFormRepository;

    public UserServiceImpl(UserRepository userRepository,
                         RoleRepository roleRepository,
                         PasswordEncoder passwordEncoder,
                         StaffService staffService,
                         MailFormRepository mailFormRepository,
                         EduroamFormRepository eduroamFormRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.staffService = staffService;
        this.mailFormRepository = mailFormRepository;
        this.eduroamFormRepository = eduroamFormRepository;
    }

    @Override
    public void saveUserStudent(StudentDto studentDto) {
        User user = new User();
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
        user.setIdentityNumber(userDto.getTcKimlikNo());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        // Personel kontrolü
        Staff staff = staffService.findByTcKimlikNo(userDto.getTcKimlikNo());
        if (staff != null) {
            List<Role> roles = new ArrayList<>();
            
            // ROLE_STAFF rolünü ekle
            Role staffRole = roleRepository.findByName("ROLE_STAFF");
            if (staffRole == null) {
                staffRole = new Role();
                staffRole.setName("ROLE_STAFF");
                roleRepository.save(staffRole);
            }
            roles.add(staffRole);
            
            // Eğer personel admin ise ROLE_ADMIN rolünü de ekle
            if (staff.isAdmin()) {
                Role adminRole = roleRepository.findByName("ROLE_ADMIN");
                if (adminRole == null) {
                    adminRole = new Role();
                    adminRole.setName("ROLE_ADMIN");
                    roleRepository.save(adminRole);
                }
                roles.add(adminRole);
                System.out.println("Admin rolü eklendi: " + staff.getTcKimlikNo()); // Debug için log
            }
            
            user.setRoles(roles);
            System.out.println("Kullanıcı rolleri: " + roles); // Debug için log
        } else {
            // ROLE_USER rolünü ata
            Role userRole = roleRepository.findByName("ROLE_USER");
            if (userRole == null) {
                userRole = new Role();
                userRole.setName("ROLE_USER");
                roleRepository.save(userRole);
            }
            user.setRoles(Arrays.asList(userRole));
        }
        
        userRepository.save(user);
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
        mailFormData.setPassword(mailFormDto.getPassword());
        mailFormData.setStatus(mailFormDto.isStatus());
        mailFormData.setApplyDate(LocalDate.now());
        mailFormRepository.save(mailFormData);
    }

    @Override
    public void saveEduroamApply(EduroamFormDto eduroamFormDto) {
        EduroamFormData eduroamFormData = new EduroamFormData();
        eduroamFormData.setUsername(eduroamFormDto.getUsername());
        eduroamFormData.setPassword(eduroamFormDto.getPassword());
        eduroamFormData.setApplyDate(LocalDate.now());
        eduroamFormData.setStatus(eduroamFormDto.isStatus());
        eduroamFormRepository.save(eduroamFormData);
    }

    @Override
    public long countUsers() {
        return userRepository.count();
    }

    @Override
    public long countActiveUsers() {
        return userRepository.count();  // Şimdilik tüm kullanıcıları aktif sayıyoruz
    }

}
