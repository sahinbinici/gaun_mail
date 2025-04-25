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
import gaun.apply.entity.form.BaseFormData;
import gaun.apply.entity.form.CloudAccountFormData;
import gaun.apply.entity.form.EduroamFormData;
import gaun.apply.entity.form.IpMacFormData;
import gaun.apply.entity.form.MailFormData;
import gaun.apply.entity.form.VpnFormData;
import gaun.apply.entity.user.User;
import gaun.apply.service.AdminTabPermissionService;
import gaun.apply.service.StaffService;
import gaun.apply.service.StudentService;
import gaun.apply.service.UserService;
import gaun.apply.service.form.CloudAccountFormService;
import gaun.apply.service.form.EduroamFormService;
import gaun.apply.service.form.FormService;
import gaun.apply.service.form.IpMacFormService;
import gaun.apply.service.form.MailFormService;
import gaun.apply.service.form.VpnFormService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;
    private final FormService formService;
    private final StudentService studentService;
    private final StaffService staffService;
    private final MailFormService mailFormService;
    private final EduroamFormService eduroamFormService;
    private final VpnFormService vpnFormService;
    private final CloudAccountFormService cloudAccountFormService;
    private final IpMacFormService ipMacFormService;
    private final AdminTabPermissionService adminTabPermissionService;

    public AdminController(UserService userService, FormService formService, StudentService studentService, StaffService staffService, MailFormService mailFormService, EduroamFormService eduroamFormService, VpnFormService vpnFormService, CloudAccountFormService cloudAccountFormService, IpMacFormService ipMacFormService, AdminTabPermissionService adminTabPermissionService) {
        this.userService = userService;
        this.formService = formService;
        this.studentService = studentService;
        this.staffService = staffService;
        this.mailFormService = mailFormService;
        this.eduroamFormService = eduroamFormService;
        this.vpnFormService = vpnFormService;
        this.cloudAccountFormService = cloudAccountFormService;
        this.ipMacFormService = ipMacFormService;
        this.adminTabPermissionService = adminTabPermissionService;
    }

    @GetMapping({"", "/"})
    public String showAdminDashboard(Model model, HttpServletRequest request, HttpSession session) {
        // Get current user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.findByidentityNumber(auth.getName());
        if (currentUser == null) {
            return "redirect:/login";
        }

        // Get tab permissions
        Map<String, Boolean> tabPermissions = adminTabPermissionService.getTabPermissions(currentUser.getId());
        model.addAttribute("tabPermissions", tabPermissions);

        // Add user statistics
        Map<String, Long> userStats = new HashMap<>();
        userStats.put("total", userService.countUsers());
        userStats.put("active", userService.countActiveUsers());
        model.addAttribute("userStats", userStats);

        // Session'dan aktif sekme bilgisini al
        String activeTab = (String) session.getAttribute("activeTab");
        if (activeTab == null || !tabPermissions.getOrDefault(activeTab, false)) {
            // Eğer aktif sekmeye erişim yoksa, erişimi olan ilk sekmeyi seç
            activeTab = tabPermissions.entrySet().stream()
                    .filter(Map.Entry::getValue)
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElse("mail");
        }
        model.addAttribute("activeTab", activeTab);

        // Load data only for permitted tabs
        if (tabPermissions.getOrDefault("mail", false)) {
            List<MailFormData> mailForms = mailFormService.getAllMailForms();
            model.addAttribute("mailForms", mailForms);
            model.addAttribute("hasMailForms", !mailForms.isEmpty());
            model.addAttribute("mailStats", getFormStats(mailForms));
            model.addAttribute("mailFormType", "mail");
        }

        if (tabPermissions.getOrDefault("eduroam", false)) {
            List<EduroamFormData> eduroamForms = eduroamFormService.getAllEduroamForms();
            model.addAttribute("eduroamForms", eduroamForms);
            model.addAttribute("hasEduroamForms", !eduroamForms.isEmpty());
            model.addAttribute("eduroamStats", getFormStats(eduroamForms));
            model.addAttribute("eduroamFormType", "eduroam");
        }

        if (tabPermissions.getOrDefault("ip-mac", false)) {
            List<IpMacFormData> ipMacForms = ipMacFormService.getAllIpMacForms();
            model.addAttribute("ipMacForms", ipMacForms);
            model.addAttribute("hasIpMacForms", !ipMacForms.isEmpty());
            model.addAttribute("ipMacStats", getFormStats(ipMacForms));
            model.addAttribute("ipMacFormType", "ipmac");
        }

        if (tabPermissions.getOrDefault("cloud", false)) {
            List<CloudAccountFormData> cloudForms = cloudAccountFormService.getAllCloudAccountForms();
            model.addAttribute("cloudForms", cloudForms);
            model.addAttribute("hasCloudForms", !cloudForms.isEmpty());
            model.addAttribute("cloudStats", getFormStats(cloudForms));
            model.addAttribute("cloudFormType", "cloud");
        }

        if (tabPermissions.getOrDefault("vpn", false)) {
            List<VpnFormData> vpnForms = vpnFormService.getAllVpnForms();
            model.addAttribute("vpnForms", vpnForms);
            model.addAttribute("hasVpnForms", !vpnForms.isEmpty());
            model.addAttribute("vpnStats", getFormStats(vpnForms));
            model.addAttribute("vpnFormType", "vpn");
        }

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

    @PostMapping("/eduroam/delete/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteEduroamForm(@PathVariable Long id) {
        try {
            eduroamFormService.deleteEduroamForm(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body("Eduroam başvurusu silinemedi: " + e.getMessage());
        }
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
                    response.put("tcKimlikNo", student.getTcKimlikNo());
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
    public ResponseEntity<?> activateForm(@PathVariable String formType, @PathVariable Long id, HttpSession session) {
        try {
            // Get current user and check permission
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = userService.findByidentityNumber(auth.getName());
            if (currentUser == null || !adminTabPermissionService.hasTabPermission(currentUser.getId(), formType)) {
                return ResponseEntity.status(403).body("Bu işlem için yetkiniz yok");
            }

            // Aktif sekme bilgisini session'a kaydet
            session.setAttribute("activeTab", formType);

            switch (formType) {
                case "mail":
                    MailFormData mailForm = mailFormService.findById(id)
                            .orElseThrow(() -> new RuntimeException("Mail başvurusu bulunamadı"));
                    mailForm.setStatus(true);
                    mailForm.setApprovalDate(LocalDateTime.now());
                    mailFormService.saveMailFormData(mailForm);
                    break;
                case "eduroam":
                    EduroamFormData eduroamForm = eduroamFormService.findById(id)
                            .orElseThrow(() -> new RuntimeException("Eduroam başvurusu bulunamadı"));
                    eduroamForm.setStatus(true);
                    eduroamForm.setApprovalDate(LocalDateTime.now());
                    eduroamFormService.saveEduroamFormData(eduroamForm);
                    break;
                case "vpn":
                    VpnFormData vpnForm = vpnFormService.findById(id)
                            .orElseThrow(() -> new RuntimeException("VPN başvurusu bulunamadı"));
                    vpnForm.setStatus(true);
                    vpnForm.setApprovalDate(LocalDateTime.now());
                    vpnFormService.saveVpnFormData(vpnForm);
                    break;
                case "ip-mac":
                    IpMacFormData ipMacForm = ipMacFormService.findById(id)
                            .orElseThrow(() -> new RuntimeException("IP-MAC başvurusu bulunamadı"));
                    ipMacForm.setStatus(true);
                    ipMacForm.setApprovalDate(LocalDateTime.now());
                    ipMacFormService.saveIpMacFormData(ipMacForm);
                    break;
                case "cloud":
                    CloudAccountFormData cloudForm = cloudAccountFormService.findById(id)
                            .orElseThrow(() -> new RuntimeException("Cloud başvurusu bulunamadı"));
                    cloudForm.setStatus(true);
                    cloudForm.setApprovalDate(LocalDateTime.now());
                    cloudAccountFormService.saveCloudAccountFormData(cloudForm);
                    break;
                default:
                    throw new RuntimeException("Geçersiz form tipi: " + formType);
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Form aktivasyonu başarısız: " + e.getMessage());
        }
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

    @PostMapping("/mail/delete/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteMailForm(@PathVariable Long id) {
        try {
            mailFormService.deleteMailForm(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body("Mail başvurusu silinemedi: " + e.getMessage());
        }
    }

    @PostMapping("/ip-mac/delete/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteIpMacForm(@PathVariable Long id) {
        try {
            ipMacFormService.deleteIpMacForm(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body("IP-MAC başvurusu silinemedi: " + e.getMessage());
        }
    }

    @PostMapping("/cloud/delete/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteCloudAccountForm(@PathVariable Long id) {
        try {
            cloudAccountFormService.deleteCloudAccountForm(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body("Cloud hesap başvurusu silinemedi: " + e.getMessage());
        }
    }

    @PostMapping("/vpn/delete/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteVpnForm(@PathVariable Long id) {
        try {
            vpnFormService.deleteVpnForm(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body("VPN başvurusu silinemedi: " + e.getMessage());
        }
    }
} 