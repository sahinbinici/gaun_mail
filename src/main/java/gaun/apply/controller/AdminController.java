package gaun.apply.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import gaun.apply.dto.StudentDto;
import gaun.apply.dto.UserDto;
import gaun.apply.entity.Staff;
import gaun.apply.entity.user.User;
import gaun.apply.entity.form.BaseFormData;
import gaun.apply.entity.form.CloudAccountFormData;
import gaun.apply.entity.form.EduroamFormData;
import gaun.apply.entity.form.IpMacFormData;
import gaun.apply.entity.form.MailFormData;
import gaun.apply.entity.form.ServerSetupFormData;
import gaun.apply.entity.form.VpnFormData;
import gaun.apply.entity.form.WebAcademicFormData;
import gaun.apply.repository.form.MailFormRepository;
import gaun.apply.service.StaffService;
import gaun.apply.service.StudentService;
import gaun.apply.service.UserService;
import gaun.apply.service.form.CloudAccountFormService;
import gaun.apply.service.form.EduroamFormService;
import gaun.apply.service.form.FormService;
import gaun.apply.service.form.IpMacFormService;
import gaun.apply.service.form.MailFormService;
import gaun.apply.service.form.ServerSetupFormService;
import gaun.apply.service.form.VpnFormService;
import gaun.apply.service.form.WebAcademicFormService;
import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;
    private final MailFormRepository mailFormRepository;
    private final FormService formService;
    private final StudentService studentService;
    private final StaffService staffService;
    private final MailFormService mailFormService;
    private final EduroamFormService eduroamFormService;
    private final VpnFormService vpnFormService;
    private final CloudAccountFormService cloudAccountFormService;
    private final IpMacFormService ipMacFormService;
    private final WebAcademicFormService webAcademicFormService;
    private final ServerSetupFormService serverSetupFormService;

    public AdminController(UserService userService, MailFormRepository mailFormRepository, FormService formService, StudentService studentService, StaffService staffService, MailFormService mailFormService, EduroamFormService eduroamFormService, VpnFormService vpnFormService, CloudAccountFormService cloudAccountFormService, IpMacFormService ipMacFormService, WebAcademicFormService webAcademicFormService, ServerSetupFormService serverSetupFormService) {
        this.userService = userService;
        this.mailFormRepository = mailFormRepository;
        this.formService = formService;
        this.studentService = studentService;
        this.staffService = staffService;
        this.mailFormService = mailFormService;
        this.eduroamFormService = eduroamFormService;
        this.vpnFormService = vpnFormService;
        this.cloudAccountFormService = cloudAccountFormService;
        this.ipMacFormService = ipMacFormService;
        this.webAcademicFormService = webAcademicFormService;
        this.serverSetupFormService = serverSetupFormService;
    }

    @GetMapping
    public String showAdminPage(Model model, HttpServletRequest request) {
        // Kullanıcı istatistikleri
        long totalUsers = userService.findAllUsers().size();
        long activeUsers = userService.findAllUsers().stream()
                .filter(UserDto::isEnabled)
                .count();
        Map<String, Long> userStats = new HashMap<>();
        userStats.put("total", totalUsers);
        userStats.put("active", activeUsers);
        model.addAttribute("userStats", userStats);

        // Mail başvuruları
        List<MailFormData> mailForms = mailFormService.getAllMailForms();
        model.addAttribute("mailForms", mailForms);
        model.addAttribute("hasMailForms", !mailForms.isEmpty());
        model.addAttribute("mailStats", getFormStats(mailForms));
        model.addAttribute("mailFormType", "mail");

        // Eduroam başvuruları
        List<EduroamFormData> eduroamForms = eduroamFormService.getAllEduroamForms();
        model.addAttribute("eduroamForms", eduroamForms);
        model.addAttribute("hasEduroamForms", !eduroamForms.isEmpty());
        model.addAttribute("eduroamStats", getFormStats(eduroamForms));
        model.addAttribute("eduroamFormType", "eduroam");

        // IP-MAC başvuruları
        List<IpMacFormData> ipMacForms = ipMacFormService.getAllIpMacForms();
        model.addAttribute("ipMacForms", ipMacForms);
        model.addAttribute("hasIpMacForms", !ipMacForms.isEmpty());
        model.addAttribute("ipMacStats", getFormStats(ipMacForms));
        model.addAttribute("ipMacFormType", "ipmac");

        // Cloud başvuruları
        List<CloudAccountFormData> cloudForms = cloudAccountFormService.getAllCloudAccountForms();
        model.addAttribute("cloudForms", cloudForms);
        model.addAttribute("hasCloudForms", !cloudForms.isEmpty());
        model.addAttribute("cloudStats", getFormStats(cloudForms));
        model.addAttribute("cloudFormType", "cloud");

        // VPN başvuruları
        List<VpnFormData> vpnForms = vpnFormService.getAllVpnForms();
        model.addAttribute("vpnForms", vpnForms);
        model.addAttribute("hasVpnForms", !vpnForms.isEmpty());
        model.addAttribute("vpnStats", getFormStats(vpnForms));
        model.addAttribute("vpnFormType", "vpn");

        // Web Akademik başvuruları
        List<WebAcademicFormData> webAcademicForms = webAcademicFormService.getAllWebAcademicForms();
        model.addAttribute("webAcademicForms", webAcademicForms);
        model.addAttribute("hasWebAcademicForms", !webAcademicForms.isEmpty());
        model.addAttribute("webAcademicStats", getFormStats(webAcademicForms));
        model.addAttribute("webAcademicFormType", "webacademic");

        // Sunucu Kurulum başvuruları
        List<ServerSetupFormData> serverSetupForms = serverSetupFormService.getAllServerSetupForms();
        model.addAttribute("serverSetupForms", serverSetupForms);
        model.addAttribute("hasServerSetupForms", !serverSetupForms.isEmpty());
        model.addAttribute("serverSetupStats", getFormStats(serverSetupForms));
        model.addAttribute("serverSetupFormType", "serversetup");

        // CSRF token'ı ekle
        CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (csrf != null) {
            model.addAttribute("_csrf", csrf);
        }

        return "admin";
    }

    private Map<String, Long> getFormStats(List<? extends BaseFormData> forms) {
        Map<String, Long> stats = new HashMap<>();
        stats.put("total", (long) forms.size());
        stats.put("pending", forms.stream()
            .filter(form -> !form.isStatus() && !form.isRejected())
            .count());
        stats.put("approved", forms.stream()
            .filter(BaseFormData::isStatus)
            .count());
        stats.put("rejected", forms.stream()
            .filter(BaseFormData::isRejected)
            .count());
        return stats;
    }

    @GetMapping("/users")
    public String users(Model model) {
        List<UserDto> users = userService.findAllUsers();
        model.addAttribute("users", users);
        return "users";
    }

    @PostMapping("/mail/activate/{id}")
    @ResponseBody
    public ResponseEntity<?> activateMailForm(@PathVariable Long id) {
        MailFormData mailForm = mailFormService.findById(id)//mailFormRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mail başvurusu bulunamadı"));
        mailForm.setStatus(true);
        mailForm.setApprovalDate(LocalDateTime.now());
        mailFormService.saveMailFormData(mailForm);//mailFormRepository.save(mailForm);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/eduroam/activate/{id}")
    @ResponseBody
    public ResponseEntity<?> activateEduroamForm(@PathVariable Long id) {
        EduroamFormData eduroamForm = eduroamFormService.findById(id)//eduroamFormRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Eduroam başvurusu bulunamadı"));
        eduroamForm.setStatus(true);
        eduroamForm.setApprovalDate(LocalDateTime.now());
        eduroamFormService.saveEduroamFormData(eduroamForm);//eduroamFormRepository.save(eduroamForm);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user-details/{username}")
    @ResponseBody
    public ResponseEntity<?> getUserDetails(@PathVariable String username) {
        try {
            // Önce user'ı bul
            User user = userService.findByidentityNumber(username);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }

            Map<String, Object> response = new HashMap<>();

            // Kullanıcı tipine göre detayları getir
            if (user.getRoles().stream().anyMatch(role -> "ROLE_USER".equals(role.getName()))) {
                StudentDto student = studentService.findByOgrenciNo(username);
                if (student != null) {
                    response.put("type", "STUDENT");
                    response.put("ogrenciNo", student.getOgrenciNo());
                    response.put("ad", student.getAd());
                    response.put("soyad", student.getSoyad());
                    response.put("fakKod", student.getFakKod());
                    response.put("bolumAd", student.getBolumAd());
                    response.put("programAd", student.getProgramAd());
                    response.put("sinif", student.getSinif());
                }
            } else {
                Staff staff = staffService.findByTcKimlikNo(username);
                if (staff != null) {
                    response.put("type", "STAFF");
                    response.put("tcKimlikNo", staff.getTcKimlikNo());
                    response.put("ad", staff.getAd());
                    response.put("soyad", staff.getSoyad());
                    response.put("birim", staff.getCalistigiBirim());
                    response.put("unvan", staff.getUnvan());
                }
            }

            if (response.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body("Kullanıcı bilgileri alınamadı: " + e.getMessage());
        }
    }

    @PostMapping("/{formType}/activate/{id}")
    @ResponseBody
    public ResponseEntity<?> activateForm(@PathVariable String formType, 
                                        @PathVariable Long id) {
        try {
            String className = formType.substring(0, 1).toUpperCase() + 
                              formType.substring(1) + "FormData";
            @SuppressWarnings("unchecked")
            Class<? extends BaseFormData> formClass = 
                (Class<? extends BaseFormData>) Class.forName("gaun.apply.entity.form." + className);
            formService.activateForm(id, formClass);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body("Form aktivasyonu başarısız: " + e.getMessage());
        }
    }

    @PostMapping("/ip-mac/activate/{id}")
    @ResponseBody
    public ResponseEntity<?> activateIpMacForm(@PathVariable Long id) {
        IpMacFormData ipMacForm = ipMacFormService.findById(id)
                .orElseThrow(() -> new RuntimeException("IP-MAC başvurusu bulunamadı"));
        ipMacForm.setStatus(true);
        ipMacForm.setApprovalDate(LocalDateTime.now());
        ipMacFormService.saveIpMacFormData(ipMacForm);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/cloud/activate/{id}")
    @ResponseBody
    public ResponseEntity<?> activateCloudForm(@PathVariable Long id) {
        CloudAccountFormData cloudForm = cloudAccountFormService.findById(id)
                .orElseThrow(() -> new RuntimeException("GAUN Bulut başvurusu bulunamadı"));
        cloudForm.setStatus(true);
        cloudForm.setApprovalDate(LocalDateTime.now());
        cloudAccountFormService.saveCloudAccountFormData(cloudForm);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/vpn/activate/{id}")
    @ResponseBody
    public ResponseEntity<?> activateVpnForm(@PathVariable Long id) {
        try {
            VpnFormData vpnForm = vpnFormService.findById(id)
                    .orElseThrow(() -> new RuntimeException("VPN başvurusu bulunamadı"));
            vpnForm.setStatus(true);
            vpnForm.setApprovalDate(LocalDateTime.now());
            vpnFormService.saveVpnFormData(vpnForm);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            // Hata detaylarını loglayalım
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body("Başvuru aktifleştirilemedi: " + e.getMessage());
        }
    }

    @PostMapping("/web-academic/activate/{id}")
    @ResponseBody
    public ResponseEntity<?> activateWebAcademicForm(@PathVariable Long id) {
        WebAcademicFormData form = webAcademicFormService.findById(id)
                .orElseThrow(() -> new RuntimeException("Web Akademik başvurusu bulunamadı"));
        form.setStatus(true);
        form.setApprovalDate(LocalDateTime.now());
        webAcademicFormService.saveWebAcademicFormData(form);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/server-setup/activate/{id}")
    @ResponseBody
    public ResponseEntity<?> activateServerSetupForm(@PathVariable Long id) {
        ServerSetupFormData form = serverSetupFormService.findById(id)
                .orElseThrow(() -> new RuntimeException("Sunucu Kurulum başvurusu bulunamadı"));
        form.setStatus(true);
        form.setApprovalDate(LocalDateTime.now());
        serverSetupFormService.saveServerSetupFormData(form);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{formType}/reject/{id}")
    @ResponseBody
    public ResponseEntity<?> rejectForm(@PathVariable String formType, 
                                      @PathVariable Long id,
                                      @RequestParam String reason) {
        try {
            // Form tipini düzgün formatlayalım
            String className = "";
            switch (formType) {
                case "mail":
                    className = "MailFormData";
                    break;
                case "eduroam":
                    className = "EduroamFormData";
                    break;
                case "ipmac":
                    className = "IpMacFormData";
                    break;
                case "cloud":
                    className = "CloudAccountFormData";
                    break;
                case "vpn":
                    className = "VpnFormData";
                    break;
                case "webacademic":
                    className = "WebAcademicFormData";
                    break;
                case "serversetup":
                    className = "ServerSetupFormData";
                    break;
                default:
                    throw new IllegalArgumentException("Geçersiz form tipi: " + formType);
            }

            @SuppressWarnings("unchecked")
            Class<? extends BaseFormData> formClass = 
                (Class<? extends BaseFormData>) Class.forName("gaun.apply.entity.form." + className);
            
            formService.rejectForm(id, formClass, reason);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body("Form reddi başarısız: " + e.getMessage());
        }
    }
} 