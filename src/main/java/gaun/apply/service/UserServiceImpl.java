package gaun.apply.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import gaun.apply.entity.form.EduroamFormData;
import gaun.apply.entity.form.MailFormData;
import gaun.apply.repository.form.EduroamFormRepository;
import gaun.apply.repository.form.MailFormRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import gaun.apply.dto.EduroamFormDto;
import gaun.apply.dto.MailFormDto;
import gaun.apply.dto.StudentDto;
import gaun.apply.dto.UserDto;
import gaun.apply.entity.Staff;
import gaun.apply.entity.user.Role;
import gaun.apply.entity.user.User;
import gaun.apply.repository.RoleRepository;
import gaun.apply.repository.UserRepository;
import static gaun.apply.util.ConvertUtil.convertEntityToDto;
import gaun.apply.entity.Student;
import gaun.apply.repository.StudentRepository;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final StaffService staffService;
    private final MailFormRepository mailFormRepository;
    private final EduroamFormRepository eduroamFormRepository;
    private final StudentRepository studentRepository;

    public UserServiceImpl(UserRepository userRepository,
                         RoleRepository roleRepository,
                         PasswordEncoder passwordEncoder,
                         StaffService staffService,
                         MailFormRepository mailFormRepository,
                         EduroamFormRepository eduroamFormRepository,
                         StudentRepository studentRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.staffService = staffService;
        this.mailFormRepository = mailFormRepository;
        this.eduroamFormRepository = eduroamFormRepository;
        this.studentRepository = studentRepository;
    }

    @Override
    public void saveUserStudent(StudentDto studentDto) {
        // User kaydı
        User user = new User();
        user.setIdentityNumber(studentDto.getOgrenciNo());
        user.setPassword(passwordEncoder.encode(studentDto.getPassword()));

        Role role = roleRepository.findByName("ROLE_USER");
        if (role == null) {
            role = new Role("ROLE_USER");
            roleRepository.save(role);
        }
        user.setRoles(Arrays.asList(role));
        userRepository.save(user);
/*
        // Student kaydı
        Student student = new Student();
        student.setOgrenciNo(studentDto.getOgrenciNo());
        student.setAd(studentDto.getAd());
        student.setSoyad(studentDto.getSoyad());
        student.setFakKod(studentDto.getFakKod());
        student.setBolumAd(studentDto.getBolumAd());
        student.setProgramAd(studentDto.getProgramAd());
        student.setSinif(studentDto.getSinif());

        studentRepository.save(student); */
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
        mailFormData.setApplyDate(LocalDateTime.now());
        mailFormRepository.save(mailFormData);
    }

    @Override
    public void saveEduroamApply(EduroamFormDto eduroamFormDto) {
        EduroamFormData eduroamFormData = new EduroamFormData();
        eduroamFormData.setUsername(eduroamFormDto.getUsername());
        eduroamFormData.setPassword(eduroamFormDto.getPassword());
        eduroamFormData.setApplyDate(LocalDateTime.now());
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
