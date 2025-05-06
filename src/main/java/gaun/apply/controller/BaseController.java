package gaun.apply.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
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
import gaun.apply.dto.SmsVerificationDto;
import gaun.apply.dto.StudentDto;
import gaun.apply.dto.UserDto;
import gaun.apply.entity.Staff;
import gaun.apply.entity.form.EduroamFormData;
import gaun.apply.entity.form.MailFormData;
import gaun.apply.entity.user.Role;
import gaun.apply.entity.user.User;
import gaun.apply.repository.RoleRepository;
import gaun.apply.repository.UserRepository;
import gaun.apply.service.SmsService;
import gaun.apply.service.StaffService;
import gaun.apply.service.StudentService;
import gaun.apply.service.UserService;
import gaun.apply.service.form.EduroamFormService;
import gaun.apply.service.form.MailFormService;
import gaun.apply.util.ConvertUtil;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

/**
 * BaseController
 */
@Controller
@RequestMapping("/")
public class BaseController {
    private final UserService userService;
    private final StudentService studentService;
    private final StaffService staffService;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final MailFormService mailFormService;
    private final EduroamFormService eduroamFormService;
    private final SmsService smsService;

    public BaseController(UserService userService,
                          StudentService studentService,
                          StaffService staffService,
                          RoleRepository roleRepository,
                          UserRepository userRepository, MailFormService mailFormService, EduroamFormService eduroamFormService, SmsService smsService) {
        this.userService = userService;
        this.studentService = studentService;
        this.staffService = staffService;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.mailFormService = mailFormService;
        this.eduroamFormService = eduroamFormService;
        this.smsService = smsService;
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

        // Get user roles and check in order of priority
        Set<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        // Check roles in priority order: ADMIN > STAFF > USER
        if (roles.contains("ROLE_ADMIN")) {
            return "redirect:/staff/index";
        } else if (roles.contains("ROLE_STAFF")) {
            return "redirect:/staff/index";
        } else if (roles.contains("ROLE_USER")) {
            return "redirect:/student/index";
        }
        // Fallback to login if no valid role found
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login(@ModelAttribute("user") User user) {
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("studentDto", new StudentDto());
        model.addAttribute("userDto", new UserDto());
        model.addAttribute("smsVerificationDto", new SmsVerificationDto());
        return "register";
    }

    @PostMapping("/register/save/student")
    public String registrationStudent(@Valid @ModelAttribute("studentDto") StudentDto studentDto,
                                    BindingResult result,
                                    HttpSession session,
                                    Model model) {
        String pass = studentDto.getPassword();
        try {
            User existingUser = userService.findByidentityNumber(studentDto.getOgrenciNo());
            if (existingUser != null) {
                result.rejectValue("ogrenciNo", null, "Bu öğrenci numarası ile daha önce kayıt yapılmış");
                model.addAttribute("userDto", new UserDto());
                model.addAttribute("smsVerificationDto", new SmsVerificationDto());
                return "register";
            }
            studentDto = ConvertUtil.getStudentFromObs(studentDto);
            studentDto.setPassword(pass);

            if (result.hasErrors()) {
                model.addAttribute("userDto", new UserDto());
                result.rejectValue("ogrenciNo", null, "OBS'den öğrenci bilgileri alınamadı");
                return "register";
            }
            // Öğrenci bilgilerini oturumda saklayın
            String verificationCode = String.valueOf(new Random().nextInt(999999));
            session.setAttribute("studentDto", studentDto);
            session.setAttribute("verificationCode", verificationCode);
            // SMS gönderimi ve diğer işlemler
            smsService.sendSms(new String[]{studentDto.getGsm1()}, "Doğrulama Kodu : "+verificationCode);

            model.addAttribute("verificationCode", verificationCode);
            model.addAttribute("smsVerificationDto", new SmsVerificationDto());
            return "sms-verification"; // Redirect to SMS verification page
        } catch (Exception e) {
            model.addAttribute("error", "Kayıt işlemi sırasında bir hata oluştu");
            model.addAttribute("userDto", new UserDto());
            model.addAttribute("smsVerificationDto", new SmsVerificationDto());
            return "register";
        }
    }

    @PostMapping("/register/verify-sms")
    public String verifySms(@Valid @ModelAttribute("smsVerificationDto") SmsVerificationDto smsVerificationDto, 
                            BindingResult result, Model model, HttpSession session){
        String code = smsVerificationDto.getCode();
        // Kod kontrolü
        if (!session.getAttribute("verificationCode").equals(code)) {
            model.addAttribute("error", "Geçersiz doğrulama kodu");
            model.addAttribute("studentDto", new StudentDto());
            model.addAttribute("userDto", new UserDto());
            return "sms-verification"; // Hata durumunda tekrar form sayfasına dön
        }
        // Öğrenci bilgilerini oturumdan al
        StudentDto studentDto = (StudentDto) session.getAttribute("studentDto");
        // Öğrenci kaydını yap
        studentService.saveStudent(studentDto);
        userService.saveUserStudent(studentDto);

        return "redirect:/register?success"; // Başarılı kayıt sonrası yönlendirme
    }

    @PostMapping("/register/save/staff")
    public String registrationStaff(@Valid @ModelAttribute("userDto") UserDto userDto,
                                  BindingResult result,
                                  Model model) {
        try {
            // Şifre eşleşme kontrolü
            if (!userDto.getPassword().equals(userDto.getConfirmPassword())) {
                result.rejectValue("confirmPassword", null, "Şifreler eşleşmiyor");
                model.addAttribute("studentDto", new StudentDto());
                model.addAttribute("userDto", new UserDto());
                return "register";
            }

            // Önce mevcut kullanıcı kontrolü
            User existingUser = userService.findByidentityNumber(userDto.getTcKimlikNo());
            if (existingUser != null) {
                result.rejectValue("tcKimlikNo", null, "Bu TC kimlik numarası ile daha önce kayıt yapılmış");
                model.addAttribute("studentDto", new StudentDto());
                model.addAttribute("userDto", new UserDto());
                model.addAttribute("error", "Bu TC kimlik numarası ile daha önce kayıt yapılmış. Lütfen farklı bir TC kimlik numarası kullanın.");
                return "register";
            }

            // Form validasyon kontrolü
            if (result.hasErrors()) {
                model.addAttribute("studentDto", new StudentDto());
                model.addAttribute("userDto", new UserDto());
                return "register";
            }

            // Personel veritabanında kontrol
            Staff staff = staffService.findByTcKimlikNo(userDto.getTcKimlikNo());
            if (staff == null) {
                result.rejectValue("tcKimlikNo", null, "Bu TC kimlik numarası ile personel kaydı bulunamadı");
                model.addAttribute("studentDto", new StudentDto());
                model.addAttribute("userDto", new UserDto());
                return "register";
            }

            // Personel kaydını yap
            userService.saveUserStaff(userDto);
            return "redirect:/register?success";
            
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Kayıt işlemi sırasında bir hata oluştu: " + e.getMessage());
            model.addAttribute("studentDto", new StudentDto());
            model.addAttribute("userDto", new UserDto());
            return "register";
        }
    }

    @GetMapping("/mail/apply")
    public String showMailApplyResult(@RequestParam(required = false) String success,
                                    @RequestParam(required = false) String error,
                                    Model model,MailFormDto mailFormDto) {
        model.addAttribute("mailFormDto", mailFormDto);
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
                return "redirect:/student/index?error=validation";
            }
            MailFormData existingMailForm = mailFormService.findByUsername(mailFormDto.getUsername());
            if (existingMailForm != null) {
                return "redirect:/student/index?mailExists=true";
            }
            userService.saveMailApply(mailFormDto);
            return "redirect:/student/index?success=true";
        } catch (Exception e) {
            return "redirect:/student/index?error=true";
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
            if (result.hasErrors()) {
                return "redirect:/student/index?error=validation";
            }

            EduroamFormData existingEduroam = eduroamFormService.eduroamFormData(eduroamFormDto.getUsername());
            if (existingEduroam != null) {
                return "redirect:/student/index?eduroamExists=true";
            }

            userService.saveEduroamApply(eduroamFormDto);
            return "redirect:/student/index?success=true";
        } catch (Exception e) {
            return "redirect:/student/index?error=true";
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
        MailFormData existingMail = mailFormService.findByUsername(username);

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
        EduroamFormData existingEduroam = eduroamFormService.eduroamFormData(username);
        
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

    @ModelAttribute
    public void addCommonAttributes(Model model, Principal principal) {
        // Varsayılan değerleri false olarak ayarla
        model.addAttribute("hasMailApplication", false);
        model.addAttribute("hasEduroamApplication", false);
        
        if (principal != null) {
            try {
                User user = userService.findByidentityNumber(principal.getName());
                if (user != null) {
                    model.addAttribute("user", user);
                    
                    // Mail başvurusu kontrolü
                    MailFormData mailForm = mailFormService.findByUsername(user.getIdentityNumber());
                    model.addAttribute("hasMailApplication", mailForm != null);
                    
                    // Eduroam başvurusu kontrolü
                    EduroamFormData eduroamForm = eduroamFormService.eduroamFormData(user.getIdentityNumber());
                    model.addAttribute("hasEduroamApplication", eduroamForm != null);
                }
            } catch (Exception e) {
                System.err.println("Error checking applications: " + e.getMessage());
            }
        }
    }

    @GetMapping("/error")
    public String errorPage() {
        return "error"; // error.html sayfasına yönlendirin
    }

    @PostMapping("/register/resend-sms")
    @ResponseBody
    public ResponseEntity<?> resendSms(HttpSession session) {
        try {
            StudentDto studentDto = (StudentDto) session.getAttribute("studentDto");
            if (studentDto == null) {
                return ResponseEntity.badRequest().body("Öğrenci bilgileri bulunamadı");
            }

            // Yeni doğrulama kodu oluştur
            String verificationCode = String.valueOf(new Random().nextInt(999999));
            // Kodu session'a kaydet
            session.setAttribute("verificationCode", verificationCode);
            
            // SMS gönder
            smsService.sendSms(new String[]{studentDto.getGsm1()}, 
                "Doğrulama kodunuz: " + verificationCode);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body("SMS gönderilemedi: " + e.getMessage());
        }
    }
} 