package gaun.apply.service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import gaun.apply.entity.form.EduroamFormData;
import gaun.apply.entity.form.MailFormData;
import gaun.apply.enums.ApplicationStatusEnum;
import gaun.apply.repository.form.EduroamFormRepository;
import gaun.apply.repository.form.MailFormRepository;
import gaun.apply.util.ConvertUtil;
import gaun.apply.util.RandomPasswordGenerator;
import gaun.apply.dto.MailFormDto;
import gaun.apply.dto.EduroamFormDto;
import gaun.apply.dto.StudentDto;
import gaun.apply.dto.UserDto;
import gaun.apply.entity.Staff;
import gaun.apply.entity.user.Role;
import gaun.apply.entity.user.User;
import gaun.apply.repository.RoleRepository;
import gaun.apply.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final StaffService staffService;
    private final MailFormRepository mailFormRepository;
    private final EduroamFormRepository eduroamFormRepository;
    private final StudentService studentService;
    private final Clock clock;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder,
                           StaffService staffService,
                           MailFormRepository mailFormRepository,
                           EduroamFormRepository eduroamFormRepository,
                           StudentService studentService,
                           Clock clock) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.staffService = staffService;
        this.mailFormRepository = mailFormRepository;
        this.eduroamFormRepository = eduroamFormRepository;
        this.studentService = studentService;
        this.clock = clock;
    }

    @Override
    public void saveUserStudent(StudentDto studentDto) {
        User user = new User();
        user.setIdentityNumber(studentDto.getOgrenciNo());
        user.setPassword(passwordEncoder.encode(studentDto.getPassword()));
        user.setTcKimlikNo(studentDto.getTcKimlikNo());
        user.setRoles(getOrCreateRole("ROLE_USER"));
        user.setActive(true);
        userRepository.save(user);
    }

    @Override
    public void saveUserStaff(UserDto userDto) {
        User user = new User();
        user.setIdentityNumber(userDto.getTcKimlikNo());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        Staff staff = staffService.findByTcKimlikNo(userDto.getTcKimlikNo());
        if (staff != null) {
            List<Role> roles = new ArrayList<>();
            roles.addAll(getOrCreateRole("ROLE_STAFF"));
            
            if (staff.isAdmin()) {
                roles.addAll(getOrCreateRole("ROLE_ADMIN"));
                logger.debug("Admin role added for staff: {}", staff.getTcKimlikNo());
            }
            
            user.setRoles(roles);
            logger.debug("User roles set: {}", roles);
        } else {
            user.setRoles(getOrCreateRole("ROLE_USER"));
        }
        user.setActive(true);
        userRepository.save(user);
    }

    private List<Role> getOrCreateRole(String roleName) {
        Role role = roleRepository.findByName(roleName);
        if (role == null) {
            role = new Role(roleName);
            roleRepository.save(role);
        }
        return Arrays.asList(role);
    }

    @Override
    public User findByidentityNumber(String identityNumber) {
        return userRepository.findByIdentityNumber(identityNumber);
    }

    @Override
    public List<UserDto> findAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(ConvertUtil::convertEntityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void saveMailApply(MailFormDto mailFormDto) {
        MailFormData mailFormData = new MailFormData();
        mailFormData.setUsername(mailFormDto.getUsername());
        mailFormData.setTcKimlikNo(mailFormDto.getTcKimlikNo());
        mailFormData.setEmail(studentService.createEmailAddress(mailFormDto.getUsername()).toLowerCase());
        mailFormData.setPassword(RandomPasswordGenerator.rastgeleSifreUret(8));
        mailFormData.setStatus(false); // Başlangıçta onaylanmamış
        mailFormData.setApplicationStatus(ApplicationStatusEnum.PENDING); // Başlangıçta beklemede
        LocalDateTime now = LocalDateTime.now(clock);
        mailFormData.setApplyDate(now);
        mailFormData.setCreatedAt(now);
        mailFormRepository.save(mailFormData);
    }

    @Override
    public void saveEduroamApply(EduroamFormDto eduroamFormDto) {
        EduroamFormData eduroamFormData = new EduroamFormData();
        eduroamFormData.setUsername(eduroamFormDto.getUsername());
        eduroamFormData.setTcKimlikNo(eduroamFormDto.getTcKimlikNo());
        eduroamFormData.setPassword(eduroamFormDto.getPassword());
        eduroamFormData.setStatus(false); // Başlangıçta onaylanmamış
        eduroamFormData.setApplicationStatus(ApplicationStatusEnum.PENDING); // Başlangıçta beklemede
        LocalDateTime now = LocalDateTime.now(clock);
        eduroamFormData.setApplyDate(now);
        eduroamFormData.setCreatedAt(now);
        eduroamFormRepository.save(eduroamFormData);
    }

    @Override
    public long countUsers() {
        return userRepository.count();
    }

    @Override
    public long countActiveUsers() {
        return userRepository.countByActive(true);
    }
}
