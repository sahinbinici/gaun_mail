package gaun.apply.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import gaun.apply.entity.user.Role;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import gaun.apply.dto.EduroamFormDto;
import gaun.apply.dto.MailFormDto;
import gaun.apply.dto.StudentDto;
import gaun.apply.dto.UserDto;
import gaun.apply.entity.EduroamFormData;
import gaun.apply.entity.MailFormData;
import gaun.apply.entity.Staff;
import gaun.apply.entity.user.User;
import gaun.apply.repository.EduroamFormRepository;
import gaun.apply.repository.MailFormRepository;
import gaun.apply.repository.RoleRepository;
import gaun.apply.repository.UserRepository;
import gaun.apply.service.StaffService;
import gaun.apply.service.StudentService;
import gaun.apply.service.UserService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/")
public class BaseController {
    private final UserService userService;
    private final StudentService studentService;
    private final MailFormRepository mailFormRepository;
    private final EduroamFormRepository eduroamFormRepository;
    private final StaffService staffService;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    public BaseController(UserService userService, 
                         StudentService studentService,
                         MailFormRepository mailFormRepository,
                         EduroamFormRepository eduroamFormRepository,
                         StaffService staffService,
                         PasswordEncoder passwordEncoder,
                         RoleRepository roleRepository,
                         UserRepository userRepository) {
        this.userService = userService;
        this.studentService = studentService;
        this.mailFormRepository = mailFormRepository;
        this.eduroamFormRepository = eduroamFormRepository;
        this.staffService = staffService;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/")
    public String root() {
        return "redirect:/student/index";
    }

    @GetMapping("/index")
    public String home(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        String identityNumber = principal.getName();
        User user = userService.findByidentityNumber(identityNumber);
        if (user == null) {
            return "redirect:/login";
        }

        // Kullanıcı rollerine göre yönlendirme
        Set<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        if (roles.contains("ROLE_ADMIN")) {
            return "redirect:/admin";  // Admin rolü varsa admin sayfasına
        } else if (roles.contains("ROLE_STAFF")) {
            return "redirect:/staff/index";  // Admin rolü yoksa ama staff rolü varsa personel sayfasına
        } else {
            return "redirect:/student/index"; // Sadece user rolü varsa öğrenci sayfasına
        }
    }

    @GetMapping("/login")
    public String login(@ModelAttribute("user") User user) {
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("studentDto", new StudentDto());
        model.addAttribute("userDto", new UserDto());
        return "register";
    }

    @PostMapping("/register/save/student")
    public String registrationStudent(@Valid @ModelAttribute("studentDto") StudentDto studentDto,
                                    BindingResult result,
                                    Model model) {
        try {
            User existingUser = userService.findByidentityNumber(studentDto.getOgrenciNo());
            if (existingUser != null) {
                result.rejectValue("ogrenciNo", null, "Bu öğrenci numarası ile daha önce kayıt yapılmış");
                return "register";
            }

            if (result.hasErrors()) {
                model.addAttribute("userDto", new UserDto());
                return "register";
            }

            userService.saveUserStudent(studentDto);
            studentService.saveStudent(studentDto);
            
            return "redirect:/register?success";
        } catch (Exception e) {
            model.addAttribute("error", "Kayıt işlemi sırasında bir hata oluştu");
            model.addAttribute("userDto", new UserDto());
            return "register";
        }
    }

    @PostMapping("/register/save/staff")
    public String registrationStaff(@Valid @ModelAttribute("userDto") UserDto userDto,
                                  BindingResult result,
                                  Model model) {
        try {
            // Önce mevcut kullanıcı kontrolü
            User existingUser = userService.findByidentityNumber(userDto.getTcKimlikNo());
            if (existingUser != null) {
                result.rejectValue("tcKimlikNo", null, "Bu TC kimlik numarası ile daha önce kayıt yapılmış");
                model.addAttribute("studentDto", new StudentDto());
                return "register";
            }

            // Form validasyon kontrolü
            if (result.hasErrors()) {
                model.addAttribute("studentDto", new StudentDto());
                return "register";
            }

            // Personel veritabanında kontrol

            Staff staff = staffService.findByTcKimlikNo(userDto.getTcKimlikNo());
            if (staff == null) {
                result.rejectValue("tcKimlikNo", null, "Bu TC kimlik numarası ile personel kaydı bulunamadı");
                model.addAttribute("studentDto", new StudentDto());
                return "register";
            }

            // Personel kaydını yap
            userService.saveUserStaff(userDto);
            return "redirect:/register?success";
            
        } catch (Exception e) {
            e.printStackTrace(); // Loglama için
            model.addAttribute("error", "Kayıt işlemi sırasında bir hata oluştu: " + e.getMessage());
            model.addAttribute("studentDto", new StudentDto());
            return "register";
        }
    }

    @GetMapping("/mail/apply")
    public String showMailApplyResult(@RequestParam(required = false) String success, 
                                    @RequestParam(required = false) String error,
                                    Model model) {
        if (error != null) {
            model.addAttribute("error", "Başvuru sırasında bir hata oluştu.");
        }
        return "mail-apply";
    }

    @PostMapping("/mail/apply")
    public String mailApply(@Valid @ModelAttribute("mailFormDto") MailFormDto mailFormDto, 
                            BindingResult result, 
                            Model model) {
        try {
            if (result.hasErrors()) {
                return "fragments/index";
            }

            MailFormData existingMailForm = mailFormRepository.findByUsername(mailFormDto.getUsername());
            if (existingMailForm != null) {
                return "redirect:/index?mailExists=true";
            }

            userService.saveMailApply(mailFormDto);
            return "redirect:/mail/apply?success";
            
        } catch (Exception e) {
            return "redirect:/index?error=true";
        }
    }

    @GetMapping("/eduroam/apply")
    public String showEduroamApplyResult(@RequestParam(required = false) String success, 
                                   @RequestParam(required = false) String error,
                                   Model model) {
        model.addAttribute("eduroamFormDto", new EduroamFormDto());
        
        if (error != null) {
            model.addAttribute("error", "Başvuru sırasında bir hata oluştu.");
        }
        return "eduroam-apply";
    }

    @PostMapping("/eduroam/apply")
    public String eduroamApply(@Valid @ModelAttribute("eduroamFormDto") EduroamFormDto eduroamFormDto,
                            BindingResult result,
                            Model model) {
        try {
            // Validasyon hataları varsa
            if (result.hasErrors()) {
                return "eduroam-apply";
            }

            // Kullanıcı adı kontrolü
            EduroamFormData existingEduroam = eduroamFormRepository.findByUsername(eduroamFormDto.getUsername());
            if (existingEduroam != null) {
                model.addAttribute("error", "Bu kullanıcı adı ile daha önce başvuru yapılmış");
                return "eduroam-apply";
            }

            userService.saveEduroamApply(eduroamFormDto);
            return "redirect:/eduroam/success";
            
        } catch (Exception e) {
            model.addAttribute("error", "Başvuru sırasında bir hata oluştu.");
            return "eduroam-apply";
        }
    }

    @GetMapping("/eduroam/success")
    public String showEduroamSuccess() {
        return "eduroam/apply-success";
    }

    @GetMapping("/check-mail-exists/{username}")
    @ResponseBody
    public Map<String, Object> checkMailExists(@PathVariable String username) {
        Map<String, Object> response = new HashMap<>();
        MailFormData existingMail = mailFormRepository.findByUsername(username);
        
        if (existingMail != null) {
            response.put("exists", true);
            response.put("email", existingMail.getEmail());
        } else {
            response.put("exists", false);
            response.put("email", null);
        }
        
        return response;
    }

    @GetMapping("/check-eduroam-exists/{username}")
    @ResponseBody
    public Map<String, Object> checkEduroamExists(@PathVariable String username) {
        Map<String, Object> response = new HashMap<>();
        EduroamFormData existingEduroam = eduroamFormRepository.findByUsername(username);
        
        if (existingEduroam != null) {
            response.put("exists", true);
            response.put("username", existingEduroam.getUsername());
        } else {
            response.put("exists", false);
            response.put("username", null);
        }
        
        return response;
    }

    @PostMapping("/admin/update-staff-role/{tcKimlikNo}")
    @ResponseBody
    public Map<String, String> updateStaffRole(@PathVariable String tcKimlikNo) {
        Map<String, String> response = new HashMap<>();
        try {
            Staff staff = staffService.findByTcKimlikNo(tcKimlikNo);
            if (staff != null) {
                staff.setAdmin(true);
                staffService.save(staff);
                
                // Kullanıcı rollerini güncelle
                User user = userService.findByidentityNumber(tcKimlikNo);
                if (user != null) {
                    List<Role> roles = new ArrayList<>(user.getRoles());
                    Role adminRole = roleRepository.findByName("ROLE_ADMIN");
                    if (adminRole == null) {
                        adminRole = new Role();
                        adminRole.setName("ROLE_ADMIN");
                        roleRepository.save(adminRole);
                    }
                    roles.add(adminRole);
                    user.setRoles(roles);
                    userRepository.save(user);
                }
                response.put("status", "success");
                response.put("message", "Admin rolü başarıyla eklendi");
            } else {
                response.put("status", "error");
                response.put("message", "Personel bulunamadı");
            }
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Hata: " + e.getMessage());
        }
        return response;
    }
} 