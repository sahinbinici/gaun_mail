package gaun.apply.domain.user.service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import gaun.apply.application.dto.*;
import gaun.apply.domain.eduroam.entity.EduroamFormData;
import gaun.apply.domain.mail.entity.MailFormData;
import gaun.apply.common.enums.ApplicationStatusEnum;
import gaun.apply.domain.eduroam.repository.EduroamFormRepository;
import gaun.apply.domain.mail.repository.MailFormRepository;
import gaun.apply.common.util.ConvertUtil;
import gaun.apply.common.util.RandomPasswordGenerator;
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
        User user = new User();
        user.setIdentityNumber(studentDto.getOgrenciNo());
        user.setPassword(passwordEncoder.encode(studentDto.getPassword()));
        user.setTcKimlikNo(studentDto.getTcKimlikNo());
        user.setSmsCode(studentDto.getSmsCode());
        user.setRoles(getOrCreateRole("ROLE_USER"));
        user.setActive(true);
        userRepository.save(user);
    }

    @Override
    public void saveUserStaff(UserDto userDto) {
        User user = new User();
        user.setIdentityNumber(userDto.getTcKimlikNo());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setTcKimlikNo(userDto.getTcKimlikNo());
        user.setSmsCode(userDto.getSmsCode());

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
/*
    @Override
    public void saveMailApply(MailFormDto mailFormDto) {
        StaffDto staffDto;
        StudentDto studentDto;
        MailFormData mailFormData = new MailFormData();
        // For students, username is typically a 12-digit student number
        if ((!mailFormDto.getOgrenciNo().isEmpty() || mailFormDto.getFakulteAd()!=null)) {
            studentDto=studentService.findByOgrenciNo(mailFormDto.getOgrenciNo());
            mailFormData.setOgrenciNo(studentDto.getOgrenciNo());
            mailFormData.setAd(studentDto.getAd());
            mailFormData.setSoyad(studentDto.getSoyad());
            mailFormData.setFakkod(studentDto.getFakKod());
            mailFormData.setBolum(studentDto.getBolumAd());
            mailFormData.setGsm(studentDto.getGsm1());
            mailFormData.setTcKimlikNo(mailFormDto.getTcKimlikNo());
            mailFormData.setEmail(studentService.createEmailAddress(mailFormDto.getOgrenciNo()).toLowerCase());
        }else {
            staffDto=staffService.findStaffDtoByTcKimlikNo(mailFormDto.getTcKimlikNo());
            mailFormData.setTcKimlikNo(mailFormDto.getTcKimlikNo());
            mailFormData.setSicil(staffDto.getSicilNo());
            mailFormData.setAd(staffDto.getAd());
            mailFormData.setSoyad(staffDto.getSoyad());
            mailFormData.setCalistigiBirim(staffDto.getCalistigiBirim());
            mailFormData.setUnvan(staffDto.getUnvan());
            mailFormData.setGsm(String.valueOf(staffDto.getGsm()));
            mailFormData.setEmail(mailFormDto.getEmail().toLowerCase());
            mailFormData.setDogumTarihi(staffDto.getDogumTarihi());
        }
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
        StaffDto staffDto;
        StudentDto studentDto;
        EduroamFormData eduroamFormData = new EduroamFormData();
        if(eduroamFormDto.getOgrenciNo()!=null && !eduroamFormDto.getOgrenciNo().isEmpty()) {
            studentDto=studentService.findByOgrenciNo(eduroamFormDto.getOgrenciNo());
            eduroamFormData.setOgrenciNo(studentDto.getOgrenciNo());
            eduroamFormData.setAd(studentDto.getAd());
            eduroamFormData.setSoyad(studentDto.getSoyad());
            eduroamFormData.setEmail(studentDto.getEposta1());
            eduroamFormData.setTcKimlikNo(eduroamFormDto.getTcKimlikNo());
            eduroamFormData.setFakulte(studentDto.getFakKod());
            eduroamFormData.setBolum(studentDto.getBolumAd());
            eduroamFormData.setGsm1(studentDto.getGsm1());
        }else {
            staffDto=staffService.findStaffDtoByTcKimlikNo(eduroamFormDto.getTcKimlikNo());
            eduroamFormData.setTcKimlikNo(String.valueOf(staffDto.getTcKimlikNo()));
            eduroamFormData.setAd(staffDto.getAd());
            eduroamFormData.setSoyad(staffDto.getSoyad());
            eduroamFormData.setSicilNo(String.valueOf(staffDto.getSicilNo()));
            eduroamFormData.setGsm(String.valueOf(staffDto.getGsm()));
            eduroamFormData.setEmail(staffDto.getEmail());
            eduroamFormData.setDogumTarihi(staffDto.getDogumTarihi());
            eduroamFormData.setCalistigiBirim(staffDto.getCalistigiBirim());
            eduroamFormData.setUnvan(staffDto.getUnvan());
        }
        eduroamFormData.setPassword(eduroamFormDto.getPassword());
        eduroamFormData.setStatus(false); // Başlangıçta onaylanmamış
        eduroamFormData.setApplicationStatus(ApplicationStatusEnum.PENDING); // Başlangıçta beklemede
        LocalDateTime now = LocalDateTime.now(clock);
        eduroamFormData.setApplyDate(now);
        eduroamFormData.setCreatedAt(now);
        eduroamFormRepository.save(eduroamFormData);
    }
    */
}
