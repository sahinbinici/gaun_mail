package gaun.apply.application.controller;

import java.security.Principal;
import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

import gaun.apply.application.dto.*;
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

import gaun.apply.domain.user.entity.Staff;
import gaun.apply.domain.eduroam.entity.EduroamFormData;
import gaun.apply.domain.mail.entity.MailFormData;
import gaun.apply.domain.user.entity.Role;
import gaun.apply.domain.user.entity.User;
import gaun.apply.domain.user.repository.RoleRepository;
import gaun.apply.domain.user.repository.UserRepository;
import gaun.apply.infrastructure.service.SmsService;
import gaun.apply.domain.user.service.StaffService;
import gaun.apply.domain.user.service.StudentService;
import gaun.apply.domain.user.service.UserService;
import gaun.apply.domain.eduroam.service.EduroamFormService;
import gaun.apply.domain.mail.service.MailFormService;
import gaun.apply.common.util.ConvertUtil;
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
                          UserRepository userRepository, 
                          MailFormService mailFormService, 
                          EduroamFormService eduroamFormService, 
                          SmsService smsService) {
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
    public String root(Model model, Principal principal) {
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
        model.addAttribute("staffDto", new StaffDto());
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
                //model.addAttribute("smsVerificationDto", new SmsVerificationDto());
                return "register";
            }else if(studentDto.getTcKimlikNo() == null || studentDto.getTcKimlikNo().isEmpty() || result.hasErrors()){
                model.addAttribute("userDto", new UserDto());
                result.rejectValue("ogrenciNo", "OBS'den öğrenci bilgileri alınamadı veya OBS şifrenizi hatalı girdiniz.", "OBS'den öğrenci bilgileri alınamadı veya OBS şifrenizi hatalı girdiniz.");
                return "register";
            }
            studentDto = ConvertUtil.getStudentFromObs(studentDto);
            studentDto.setPassword(pass);
/*
            if (result.hasErrors()) {
                model.addAttribute("userDto", new UserDto());
                result.rejectValue("ogrenciNo", "OBS'den öğrenci bilgileri alınamadı veya OBS şifrenizi hatalı girdiniz.", "OBS'den öğrenci bilgileri alınamadı veya OBS şifrenizi hatalı girdiniz.");
                return "register";
            }
*/
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
            model.addAttribute(session.getAttribute("studentDto") != null ? "studentDto" : "staffDto", 
                  session.getAttribute("studentDto") != null ? new StudentDto() : new StaffDto());
            model.addAttribute("userDto", new UserDto());
            return "sms-verification"; // Hata durumunda tekrar form sayfasına dön
        }

        if(session.getAttribute("staffDto") != null){
            StaffDto staffDto = (StaffDto) session.getAttribute("staffDto");
            UserDto userDto = (UserDto) session.getAttribute("userDto");
            userDto.setTcKimlikNo(String.valueOf(staffDto.getTcKimlikNo()));
            staffService.saveStaff(staffDto);
            userService.saveUserStaff(userDto);
        }else if(session.getAttribute("studentDto") != null){
            // Öğrenci bilgilerini oturumdan al
            StudentDto studentDto = (StudentDto) session.getAttribute("studentDto");
            // Öğrenci kaydını yap
            studentService.saveStudent(studentDto);
            userService.saveUserStudent(studentDto);
        }
        return "redirect:/register?success"; // Başarılı kayıt sonrası yönlendirme
    }

    @PostMapping("/register/save/staff")
    public String registrationStaff(@Valid @ModelAttribute("userDto") UserDto userDto,
                                  BindingResult result,HttpSession session,
                                  Model model) {
        try {
            // Önce mevcut kullanıcı kontrolü
            User existingUser = userService.findByTcKimlikNo(userDto.getTcKimlikNo());
            if (existingUser != null) {
                result.rejectValue("tcKimlikNo", null, "Bu TC kimlik numarası ile daha önce kayıt yapılmış");
                model.addAttribute("staffDto", new StaffDto());
                model.addAttribute("userDto", new UserDto());
                model.addAttribute("activeTab", "staff");
                return "register";
            }
            // Şifre eşleşme kontrolü
            if (!userDto.getPassword().equals(userDto.getConfirmPassword())) {
                result.rejectValue("confirmPassword", null, "Şifreler eşleşmiyor");
                model.addAttribute("staffDto", new StaffDto());
                model.addAttribute("userDto", new UserDto());
                model.addAttribute("activeTab", "staff");
                return "register";
            }
            StaffDto staffDto = staffService.findStaffDtoByTcKimlikNo(userDto.getTcKimlikNo());
            if (staffDto == null) {
                result.rejectValue("tcKimlik", null, "Personel veritabanında kaydınız bulunamadı.");
                model.addAttribute("activeTab", "staff");
                return "register";
            }
            // staff bilgilerini oturumda saklayın
            String verificationCode = String.valueOf(new Random().nextInt(999999));
            session.setAttribute("staffDto", staffDto);
            session.setAttribute("verificationCode", verificationCode);
            session.setAttribute("userDto", userDto);
            // SMS gönderimi ve diğer işlemler
            smsService.sendSms(new String[]{String.valueOf(staffDto.getGsm())}, "Doğrulama Kodu : "+verificationCode);

            model.addAttribute("verificationCode", verificationCode);
            model.addAttribute("smsVerificationDto", new SmsVerificationDto());
            return "sms-verification"; // Redirect to SMS verification page
        } catch (Exception e) {
            model.addAttribute("error", "Kayıt işlemi sırasında bir hata oluştu: " + e.getMessage());
            model.addAttribute("studentDto", new StudentDto());
            model.addAttribute("userDto", new UserDto());
            model.addAttribute("activeTab", "staff");
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
                        Model model,
                        Principal principal) {
        try {
            if (result.hasErrors()) {
                return determineRedirectUrl(principal, "?error=validation");
            }
            MailFormData existingMailForm = mailFormService.findByTcKimlikNo(mailFormDto.getTcKimlikNo());
            if (existingMailForm != null) {
                return determineRedirectUrl(principal, "?mailExists=true");
            }
            userService.saveMailApply(mailFormDto);
            return determineRedirectUrl(principal, "?success=true");
        } catch (Exception e) {
            return determineRedirectUrl(principal, "?error=true");
        }
    }

    /**
     * Helper method to determine the appropriate redirect URL based on user role
     */
    private String determineRedirectUrl(Principal principal, String queryParams) {
        if (principal == null) {
            return "redirect:/login";
        }
        
        String username = principal.getName();
        User user = userService.findByidentityNumber(username);
        
        if (user != null && user.getRoles() != null) {
            boolean isStaff = user.getRoles().stream()
                .anyMatch(role -> "ROLE_STAFF".equals(role.getName()));
            
            if (isStaff) {
                return "redirect:/staff/index" + queryParams;
            }
        }
        
        // Default to student redirect
        return "redirect:/student/index" + queryParams;
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
                        Model model,
                        Principal principal) {
        try {
            if (result.hasErrors()) {
                return determineRedirectUrl(principal, "?error=validation");
            }

            EduroamFormData existingEduroam = eduroamFormService.eduroamFormData(eduroamFormDto.getTcKimlikNo());
            if (existingEduroam != null) {
                return determineRedirectUrl(principal, "?eduroamExists=true");
            }

            userService.saveEduroamApply(eduroamFormDto);
            return determineRedirectUrl(principal, "?success=true");
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception for debugging
            return determineRedirectUrl(principal, "?error=true&message=" + e.getMessage());
        }
    }

    @GetMapping("/eduroam/success")
    public String showEduroamSuccess() {
        return "eduroam/apply-success";
    }

    @GetMapping("/check-mail-exists/{tcKimlikNo}")
    @ResponseBody
    public Map<String, Object> checkMailExists(@PathVariable String tcKimlikNo) {
        Map<String, Object> response = new HashMap<>();
        MailFormData existingMail = mailFormService.findByTcKimlikNo(tcKimlikNo);

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
            response.put("tcKimlikNo", existingEduroam.getTcKimlikNo());
        } else {
            response.put("exists", false);
            response.put("tcKimlikNo", null);
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
                    MailFormData mailForm = mailFormService.findByTcKimlikNo(user.getTcKimlikNo());
                    model.addAttribute("hasMailApplication", mailForm != null);
                    
                    // Eduroam başvurusu kontrolü
                    EduroamFormData eduroamForm = eduroamFormService.eduroamFormData(user.getTcKimlikNo());
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