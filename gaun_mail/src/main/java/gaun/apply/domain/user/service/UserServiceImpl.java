package gaun.apply.domain.user.service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import gaun.apply.application.dto.*;
import gaun.apply.domain.eduroam.entity.EduroamFormData;
import gaun.apply.domain.eduroam.repository.EduroamFormRepository;
import gaun.apply.domain.mail.entity.MailFormData;
import gaun.apply.domain.mail.repository.MailFormRepository;
import gaun.apply.common.util.ConvertUtil;
import gaun.apply.domain.user.entity.Staff;
import gaun.apply.domain.user.entity.Role;
import gaun.apply.domain.user.entity.User;
import gaun.apply.domain.user.repository.RoleRepository;
import gaun.apply.domain.user.repository.StaffRepository;
import gaun.apply.domain.user.repository.UserRepository;
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
    private final StaffRepository staffRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder,
                           StaffService staffService,
                           MailFormRepository mailFormRepository,
                           EduroamFormRepository eduroamFormRepository,
                           StudentService studentService,
                           Clock clock, StaffRepository staffRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.staffService = staffService;
        this.mailFormRepository = mailFormRepository;
        this.eduroamFormRepository = eduroamFormRepository;
        this.studentService = studentService;
        this.clock = clock;
        this.staffRepository = staffRepository;
    }

    @Override
    public void saveUserStudent(StudentDto studentDto) {
        // Check if user already exists with this identity number or TCKN
        if (userRepository.existsByIdentityNumberOrTcKimlikNo(studentDto.getOgrenciNo(), studentDto.getTcKimlikNo())) {
            throw new RuntimeException("Bu öğrenci numarası veya TC Kimlik numarası ile daha önce kayıt yapılmıştır.");
        }

        User user = new User();
        user.setIdentityNumber(studentDto.getOgrenciNo());
        user.setPassword(passwordEncoder.encode(studentDto.getPassword()));
        user.setTcKimlikNo(studentDto.getTcKimlikNo());
        user.setAd(studentDto.getAd());
        user.setSoyad(studentDto.getSoyad());
        user.setSmsCode(studentDto.getSmsCode());
        user.setSmsVerified(studentDto.isSmsVerified());
        user.setRoles(getOrCreateRole("ROLE_USER"));
        user.setActive(true);
        userRepository.save(user);
    }

    @Override
    public void saveUserStaff(UserDto userDto) {
        // Check if user already exists with this TCKN
        if (userRepository.existsByTcKimlikNo(userDto.getTcKimlikNo())) {
            throw new RuntimeException("Bu TC Kimlik numarası ile daha önce kayıt yapılmıştır.");
        }

        User user = new User();
        user.setIdentityNumber(userDto.getTcKimlikNo());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setTcKimlikNo(userDto.getTcKimlikNo());
        user.setSmsCode(userDto.getSmsCode());
        user.setSmsVerified(userDto.isSmsVerified());

        Staff staff = staffService.findByTcKimlikNo(userDto.getTcKimlikNo());
        if (staff != null) {
            user.setAd(staff.getAd());
            user.setSoyad(staff.getSoyad());

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

    public User findByTcKimlikNo(String tcKimlikNo) {
        return userRepository.findByTcKimlikNo(tcKimlikNo);
    }

    @Override
    public void updatePassword(String tcKimlikNo, String newPassword) {
        User user = userRepository.findByTcKimlikNo(tcKimlikNo);
        if (user != null) {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
        }
    }

    @Override
    public List<UserDto> findAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(ConvertUtil::convertEntityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> findAllUsersEntity() {
        return userRepository.findAll();
    }

    @Override
    public List<User> findRecentUsers(int days) {
        LocalDate cutoffDate = LocalDate.now().minusDays(days);
        return userRepository.findByRegisterDateAfterOrderByRegisterDateDesc(cutoffDate);
    }

    @Override
    public List<User> findTop50Users() {
        return userRepository.findTop50ByOrderByRegisterDateDesc();
    }

    @Override
    public List<User> searchUsers(String query) {
        return userRepository.searchUsers(query);
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        String tcKimlikNo = user.getTcKimlikNo();

        // 1. Mail form başvurularını sil
        List<MailFormData> mailForms = mailFormRepository.findAllByTcKimlikNo(tcKimlikNo);
        mailForms.forEach(mailForm -> {
            mailFormRepository.delete(mailForm);
            logger.info("Mail form silindi: {}", mailForm.getId());
        });

        // 2. Eduroam form başvurularını sil
        List<EduroamFormData> eduroamForms = eduroamFormRepository.findAllByTcKimlikNo(tcKimlikNo);
        eduroamForms.forEach(eduroamForm -> {
            eduroamFormRepository.delete(eduroamForm);
            logger.info("Eduroam form silindi: {}", eduroamForm.getId());
        });

        // 3. Rolleri temizle (users_roles tablosu)
        user.getRoles().clear();
        userRepository.save(user);

        // 4. Kullanıcıyı sil (users tablosu)
        userRepository.deleteById(id);

        logger.info("Kullanıcı ve tüm ilişkili verileri silindi: {} (TC: {})", user.getIdentityNumber(), tcKimlikNo);
    }
}
