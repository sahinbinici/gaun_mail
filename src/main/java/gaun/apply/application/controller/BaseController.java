package gaun.apply.application.controller;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

import gaun.apply.application.dto.PasswordResetDto;
import gaun.apply.application.dto.SmsVerificationDto;
import gaun.apply.application.dto.StaffDto;
import gaun.apply.application.dto.StudentDto;
import gaun.apply.application.dto.EduroamFormDto;
import gaun.apply.application.dto.MailFormDto;
import gaun.apply.application.dto.UserDto;
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
            // Check for validation errors first
            if (result.hasErrors()) {
                // Add all necessary model attributes for proper form rendering
                model.addAttribute("userDto", new UserDto());
                model.addAttribute("staffDto", new StaffDto());
                model.addAttribute("smsVerificationDto", new SmsVerificationDto());
                model.addAttribute("activeTab", "student");
                return "register";
            }
            
            User existingUser = userService.findByidentityNumber(studentDto.getOgrenciNo());
            if (existingUser != null) {
                result.rejectValue("ogrenciNo", null, "Bu öğrenci numarası ile daha önce kayıt yapılmış");
                model.addAttribute("userDto", new UserDto());
                model.addAttribute("staffDto", new StaffDto());
                model.addAttribute("smsVerificationDto", new SmsVerificationDto());
                model.addAttribute("activeTab", "student");
                return "register";
            }
            
            studentDto = ConvertUtil.getStudentFromObs(studentDto);
            studentDto.setPassword(pass);

            if (studentDto.getOgrenciNo() == null) {
                result.rejectValue("ogrenciNo", null, "Öğrenci numarası veya OBS şifresi yanlış!!!");
                model.addAttribute("userDto", new UserDto());
                model.addAttribute("staffDto", new StaffDto());
                model.addAttribute("smsVerificationDto", new SmsVerificationDto());
                model.addAttribute("activeTab", "student");
                return "register";
            }

            // Öğrenci bilgilerini oturumda saklayın
            String verificationCode = String.valueOf(new Random().nextInt(999999));
            session.setAttribute("studentDto", studentDto);
            session.setAttribute("verificationCode", verificationCode);
            // SMS gönderimi ve diğer işlemler
            smsService.sendSms(new String[]{studentDto.getGsm1()}, "Doğrulama Kodu : "+verificationCode);
            model.addAttribute("gsm", studentDto.getGsm1());
            model.addAttribute("verificationCode", verificationCode);
            model.addAttribute("smsVerificationDto", new SmsVerificationDto());
            return "sms-verification"; // Redirect to SMS verification page
        } catch (Exception e) {
            //model.addAttribute("error", "Kayıt işlemi sırasında bir hata oluştu: " + e.getMessage());
            result.rejectValue("ogrenciNo", null, "Öğrenci bilgileri OBS sisteminden alınamadı. Bilgi İşlem Daire Başkanlığı ile iletişime geçin.");
            // Ensure studentDto retains the submitted values for display
            model.addAttribute("studentDto", studentDto);
            model.addAttribute("userDto", new UserDto());
            model.addAttribute("staffDto", new StaffDto());
            model.addAttribute("smsVerificationDto", new SmsVerificationDto());
            model.addAttribute("activeTab", "student");
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
            userDto.setSmsCode(code);
            staffService.saveStaff(staffDto);
            userService.saveUserStaff(userDto);
        }else if(session.getAttribute("studentDto") != null){
            // Öğrenci bilgilerini oturumdan al
            StudentDto studentDto = (StudentDto) session.getAttribute("studentDto");
            studentDto.setSmsCode(code);
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
            // Check for validation errors first
            if (result.hasErrors()) {
                // Add all necessary model attributes for proper form rendering
                model.addAttribute("staffDto", new StaffDto());
                model.addAttribute("studentDto", new StudentDto());
                model.addAttribute("userDto", userDto);
                model.addAttribute("smsVerificationDto", new SmsVerificationDto());
                model.addAttribute("activeTab", "staff");
                return "register";
            }

            // Şifre eşleşme kontrolü
            if (!userDto.getPassword().equals(userDto.getConfirmPassword())) {
                result.rejectValue("confirmPassword", null, "Şifreler eşleşmiyor");
                model.addAttribute("staffDto", new StaffDto());
                model.addAttribute("studentDto", new StudentDto());
                model.addAttribute("smsVerificationDto", new SmsVerificationDto());
                model.addAttribute("activeTab", "staff");
                return "register";
            }
            
            // Önce mevcut kullanıcı kontrolü
            StaffDto staffDto = staffService.findStaffDtoByTcKimlikNo(userDto.getTcKimlikNo());
            User existingUser = userService.findByTcKimlikNo(userDto.getTcKimlikNo());
            if (existingUser != null) {
                result.rejectValue("tcKimlikNo", null, "Bu TC kimlik numarası ile daha önce kayıt yapılmış");
                model.addAttribute("staffDto", new StaffDto());
                model.addAttribute("studentDto", new StudentDto());
                model.addAttribute("smsVerificationDto", new SmsVerificationDto());
                model.addAttribute("activeTab", "staff");
                return "register";
            }else if(staffDto==null){
                result.rejectValue("tcKimlikNo", null, "Girilen TC Kimlik numarası ile personel bilgisi bulunamadı. Bilgi İşlem Daire Başkanlığı ile iletişime geçin.");
                model.addAttribute("staffDto", new StaffDto());
                model.addAttribute("studentDto", new StudentDto());
                model.addAttribute("smsVerificationDto", new SmsVerificationDto());
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
            model.addAttribute("gsm", staffDto.getGsm());
            model.addAttribute("verificationCode", verificationCode);
            model.addAttribute("smsVerificationDto", new SmsVerificationDto());
            return "sms-verification"; // Redirect to SMS verification page
        } catch (Exception e) {
            result.rejectValue("tcKimlikNo", null, "Girilen TC Kimlik numarası ile personel bilgisi bulunamadı. Bilgi İşlem Daire Başkanlığı ile iletişime geçin.");
            model.addAttribute("userDto", userDto);
            model.addAttribute("studentDto", new StudentDto());
            model.addAttribute("staffDto", new StaffDto());
            model.addAttribute("smsVerificationDto", new SmsVerificationDto());
            model.addAttribute("activeTab", "staff");
            return "register";
        }
    }
/*
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
*/
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
            mailFormService.saveMailApply(mailFormDto);
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

            eduroamFormService.saveEduroamApply(eduroamFormDto);
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

    @GetMapping("/forgot-password-staff")
    public String showForgotPasswordStaffForm(Model model) {
        return "forgot-password-staff";
    }

    @PostMapping("/forgot-password-staff")
    public String handleForgotPasswordStaff(@RequestParam("tcKimlikNo") String tcKimlikNo, HttpSession session, Model model) {
        try {
            StaffDto staffDto = staffService.findStaffDtoByTcKimlikNo(tcKimlikNo);
            if (staffDto == null) {
                model.addAttribute("error", "Bu TC Kimlik Numarası ile kayıtlı bir personel bulunamadı.");
                return "forgot-password-staff";
            }

            String verificationCode = String.valueOf(new Random().nextInt(999999));
            session.setAttribute("resetPasswordTcKimlikNo", tcKimlikNo);
            session.setAttribute("verificationCode", verificationCode);

            smsService.sendSms(new String[]{String.valueOf(staffDto.getGsm())}, "Şifre Sıfırlama Doğrulama Kodu: " + verificationCode);

            model.addAttribute("smsVerificationDto", new SmsVerificationDto());
            return "sms-verification-password-reset";
        } catch (Exception e) {
            model.addAttribute("error", "İşlem sırasında bir hata oluştu: " + e.getMessage());
            return "forgot-password-staff";
        }
    }

    @PostMapping("/verify-sms-password-reset")
    public String verifySmsForPasswordReset(@Valid @ModelAttribute("smsVerificationDto") SmsVerificationDto smsVerificationDto,
                                            BindingResult result, Model model, HttpSession session) {
        String code = smsVerificationDto.getCode();
        String sessionCode = (String) session.getAttribute("verificationCode");

        if (sessionCode == null || !sessionCode.equals(code)) {
            model.addAttribute("error", "Geçersiz doğrulama kodu.");
            return "sms-verification-password-reset";
        }

        // Kod doğru, şifre sıfırlama sayfasına yönlendir
        model.addAttribute("passwordResetDto", new PasswordResetDto());
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String handleResetPassword(@Valid @ModelAttribute("passwordResetDto") PasswordResetDto passwordResetDto,
                                      BindingResult result, Model model, HttpSession session) {
        if (!passwordResetDto.getPassword().equals(passwordResetDto.getConfirmPassword())) {
            model.addAttribute("error", "Şifreler eşleşmiyor.");
            return "reset-password";
        }

        String tcKimlikNo = (String) session.getAttribute("resetPasswordTcKimlikNo");
        if (tcKimlikNo == null) {
            model.addAttribute("error", "Oturum süresi dolmuş veya geçersiz. Lütfen tekrar deneyin.");
            return "forgot-password-staff";
        }

        try {
            userService.updatePassword(tcKimlikNo, passwordResetDto.getPassword());
            session.removeAttribute("resetPasswordTcKimlikNo");
            session.removeAttribute("verificationCode");
            return "redirect:/login?passwordResetSuccess=true";
        } catch (Exception e) {
            model.addAttribute("error", "Şifre sıfırlanırken bir hata oluştu: " + e.getMessage());
            return "reset-password";
        }
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