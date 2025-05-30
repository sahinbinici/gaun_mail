package gaun.apply.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gaun.apply.enums.ApplicationStatusEnum;
import gaun.apply.dto.StaffDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
import gaun.apply.service.SmsService;
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
import org.springframework.http.HttpStatus;

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
    private final SmsService smsService;

    public AdminController(UserService userService,
                         FormService formService,
                         StudentService studentService,
                         StaffService staffService,
                         MailFormService mailFormService,
                         EduroamFormService eduroamFormService,
                         VpnFormService vpnFormService,
                         CloudAccountFormService cloudAccountFormService,
                         IpMacFormService ipMacFormService,
                         AdminTabPermissionService adminTabPermissionService,
                         SmsService smsService) {
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
        this.smsService = smsService;
    }

    @GetMapping({"", "/"})
    public String showAdminDashboard(Model model, HttpServletRequest request, HttpSession session,
                                    @RequestParam(required = false) String filter,
                                    @RequestParam(required = false) String tcKimlikNo,
                                    @RequestParam(required = false) String status) {
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
        
        // Add filter values to model
        model.addAttribute("currentFilter", filter);
        model.addAttribute("currentTcKimlikNo", tcKimlikNo);
        model.addAttribute("currentStatus", status);

        // Load data only for permitted tabs
        if (tabPermissions.getOrDefault("mail", false)) {
            List<MailFormData> mailForms;
            if (tcKimlikNo != null && !tcKimlikNo.isEmpty()) {
                MailFormData mailForm = mailFormService.findByTcKimlikNo(tcKimlikNo);
                mailForms = mailForm != null ? List.of(mailForm) : List.of();
            } else if (status != null && !status.isEmpty()) {
                mailForms = mailFormService.findByDurum(status);
            } else if ("month".equals(filter)) {
                mailForms = mailFormService.findLastMonthApplications();
            } else if ("all".equals(filter)) {
                mailForms = mailFormService.getAllMailForms();
            } else {
                // Default to last 100 applications
                mailForms = mailFormService.findLast100Applications();
            }
            model.addAttribute("mailForms", mailForms);
            model.addAttribute("hasMailForms", !mailForms.isEmpty());
            model.addAttribute("mailStats", getFormStats(mailForms));
            model.addAttribute("mailFormType", "mail");
        }

        if (tabPermissions.getOrDefault("eduroam", false)) {
            List<EduroamFormData> eduroamForms;
            if (tcKimlikNo != null && !tcKimlikNo.isEmpty()) {
                EduroamFormData eduroamForm = eduroamFormService.findByTcKimlikNo(tcKimlikNo);
                eduroamForms = eduroamForm != null ? List.of(eduroamForm) : List.of();
            } else if (status != null && !status.isEmpty()) {
                eduroamForms = eduroamFormService.findByDurum(status);
            } else if ("month".equals(filter)) {
                eduroamForms = eduroamFormService.findLastMonthApplications();
            } else if ("all".equals(filter)) {
                eduroamForms = eduroamFormService.getAllEduroamForms();
            } else {
                // Default to last 100 applications
                eduroamForms = eduroamFormService.findLast100Applications();
            }
            model.addAttribute("eduroamForms", eduroamForms);
            model.addAttribute("hasEduroamForms", !eduroamForms.isEmpty());
            model.addAttribute("eduroamStats", getFormStats(eduroamForms));
            model.addAttribute("eduroamFormType", "eduroam");
        }

        if (tabPermissions.getOrDefault("ip-mac", false)) {
            List<IpMacFormData> ipMacForms;
            if (tcKimlikNo != null && !tcKimlikNo.isEmpty()) {
                IpMacFormData ipMacForm = ipMacFormService.findByTcKimlikNo(tcKimlikNo);
                ipMacForms = ipMacForm != null ? List.of(ipMacForm) : List.of();
            } else if (status != null && !status.isEmpty()) {
                ipMacForms = ipMacFormService.findByDurum(status);
            } else if ("month".equals(filter)) {
                ipMacForms = ipMacFormService.findLastMonthApplications();
            } else if ("all".equals(filter)) {
                ipMacForms = ipMacFormService.getAllIpMacForms();
            } else {
                // Default to last 100 applications
                ipMacForms = ipMacFormService.findLast100Applications();
            }
            model.addAttribute("ipMacForms", ipMacForms);
            model.addAttribute("hasIpMacForms", !ipMacForms.isEmpty());
            model.addAttribute("ipMacStats", getFormStats(ipMacForms));
            model.addAttribute("ipMacFormType", "ipmac");
        }

        if (tabPermissions.getOrDefault("cloud", false)) {
            List<CloudAccountFormData> cloudForms;
            if (tcKimlikNo != null && !tcKimlikNo.isEmpty()) {
                CloudAccountFormData cloudForm = cloudAccountFormService.findByTcKimlikNo(tcKimlikNo);
                cloudForms = cloudForm != null ? List.of(cloudForm) : List.of();
            } else if (status != null && !status.isEmpty()) {
                cloudForms = cloudAccountFormService.findByDurum(status);
            } else if ("month".equals(filter)) {
                cloudForms = cloudAccountFormService.findLastMonthApplications();
            } else if ("all".equals(filter)) {
                cloudForms = cloudAccountFormService.getAllCloudAccountForms();
            } else {
                // Default to last 100 applications
                cloudForms = cloudAccountFormService.findLast100Applications();
            }
            model.addAttribute("cloudForms", cloudForms);
            model.addAttribute("hasCloudForms", !cloudForms.isEmpty());
            model.addAttribute("cloudStats", getFormStats(cloudForms));
            model.addAttribute("cloudFormType", "cloud");
        }

        if (tabPermissions.getOrDefault("vpn", false)) {
            List<VpnFormData> vpnForms;
            if (tcKimlikNo != null && !tcKimlikNo.isEmpty()) {
                VpnFormData vpnForm = vpnFormService.findByTcKimlikNo(tcKimlikNo);
                vpnForms = vpnForm != null ? List.of(vpnForm) : List.of();
            } else if (status != null && !status.isEmpty()) {
                vpnForms = vpnFormService.findByDurum(status);
            } else if ("month".equals(filter)) {
                vpnForms = vpnFormService.findLastMonthApplications();
            } else if ("all".equals(filter)) {
                vpnForms = vpnFormService.getAllVpnForms();
            } else {
                // Default to last 100 applications
                vpnForms = vpnFormService.findLast100Applications();
            }
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
        MailFormData mailForm = mailFormService.findMailFormById(id)
                .orElseThrow(() -> new RuntimeException("Mail başvurusu bulunamadı"));
        mailForm.setStatus(true);
        mailForm.setApplicationStatus(ApplicationStatusEnum.APPROVED); // Set application status to APPROVED
        mailForm.setApprovalDate(LocalDateTime.now());
        mailFormService.save(mailForm);
        
        // Try to find by username first (for students this would be the student number)
        StudentDto studentDto = studentService.findByOgrenciNo(mailForm.getUsername());
        
        // If not found and we have a TC Kimlik No, try that for staff
        StaffDto staffDto = null;
        if (studentDto == null && mailForm.getTcKimlikNo() != null) {
            staffDto = staffService.findByStaffTCKimlikNo(mailForm.getTcKimlikNo());
        }
        
        // Send appropriate SMS notification based on user type
        if (studentDto != null && studentDto.getOgrenciNo() != null && studentDto.getOgrenciNo().length() == 12) {
            smsService.sendSms(new String[]{studentDto.getGsm1()}, "GAÜN E-posta başvurunuz onaylandı.https://mail2.gaziantep.edu.tr adresinden oturum açıp şifrenizi değiştirin.");
        } else if (staffDto != null && staffDto.getTcKimlikNo() != null && staffDto.getTcKimlikNo().length() == 11) {
            smsService.sendSms(new String[]{staffDto.getGsm()}, "GAÜN E-posta başvurunuz onaylandı.https://mail1.gaziantep.edu.tr/ adresinden oturum açıp şifrenizi değiştirin.");
        }
        
        return ResponseEntity.ok().build();
    }

    @PostMapping("/eduroam/activate/{id}")
    @ResponseBody
    public ResponseEntity<?> activateEduroamForm(@PathVariable Long id, Model model) {
        try {
            EduroamFormData eduroamForm = eduroamFormService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Eduroam başvurusu bulunamadı"));
            eduroamForm.setStatus(true);
            eduroamForm.setApplicationStatus(ApplicationStatusEnum.APPROVED); // Set application status to APPROVED
            eduroamForm.setApprovalDate(LocalDateTime.now());
            eduroamFormService.saveEduroamFormData(eduroamForm);
            
            // Try to find by username first (for students this would be the student number)
            StudentDto studentDto = studentService.findByOgrenciNo(eduroamForm.getUsername());
            
            // If not found and we have a TC Kimlik No, try that for staff
            StaffDto staffDto = null;
            if (studentDto == null && eduroamForm.getTcKimlikNo() != null) {
                staffDto = staffService.findByStaffTCKimlikNo(eduroamForm.getTcKimlikNo());
            }
            
            // Send appropriate SMS notification based on user type
            if (studentDto != null && studentDto.getOgrenciNo() != null && studentDto.getOgrenciNo().length() == 12) {
                smsService.sendSms(new String[]{studentDto.getGsm1()}, "GAÜN Eduroam başvurunuz onaylandı");
            } else if (staffDto != null && staffDto.getTcKimlikNo() != null) {
                smsService.sendSms(new String[]{staffDto.getGsm()}, "GAÜN Eduroam başvurunuz onaylandı");
            }
            
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Eduroam başvurusu onaylanamadı: " + e.getMessage());
        }
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
                    response.put("email", staff.getEmail());
                    response.put("gsm", staff.getGsm());
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
                    MailFormData mailForm = mailFormService.findMailFormById(id)
                            .orElseThrow(() -> new RuntimeException("Mail başvurusu bulunamadı"));
                    mailForm.setStatus(true);
                    mailForm.setApplicationStatus(ApplicationStatusEnum.APPROVED);
                    mailForm.setApprovalDate(LocalDateTime.now());
                    mailFormService.save(mailForm);
                    return ResponseEntity.ok("Mail başvurusu onaylandı");
                case "eduroam":
                    EduroamFormData eduroamForm = eduroamFormService.findById(id)
                            .orElseThrow(() -> new RuntimeException("Eduroam başvurusu bulunamadı"));
                    eduroamForm.setStatus(true);
                    eduroamForm.setApplicationStatus(ApplicationStatusEnum.APPROVED);
                    eduroamForm.setApprovalDate(LocalDateTime.now());
                    eduroamFormService.saveEduroamFormData(eduroamForm);
                    return ResponseEntity.ok("Eduroam başvurusu onaylandı");
                case "vpn":
                    VpnFormData vpnForm = vpnFormService.findById(id)
                            .orElseThrow(() -> new RuntimeException("VPN başvurusu bulunamadı"));
                    vpnForm.setStatus(true);
                    vpnForm.setApplicationStatus(ApplicationStatusEnum.APPROVED);
                    vpnForm.setApprovalDate(LocalDateTime.now());
                    vpnFormService.saveVpnFormData(vpnForm);
                    return ResponseEntity.ok("VPN başvurusu onaylandı");
                case "ip-mac":
                    IpMacFormData ipMacForm = ipMacFormService.findById(id)
                            .orElseThrow(() -> new RuntimeException("IP-MAC başvurusu bulunamadı"));
                    ipMacForm.setStatus(true);
                    ipMacForm.setApplicationStatus(ApplicationStatusEnum.APPROVED);
                    ipMacForm.setApprovalDate(LocalDateTime.now());
                    ipMacFormService.saveIpMacFormData(ipMacForm);
                    return ResponseEntity.ok("IP-MAC başvurusu onaylandı");
                case "cloud":
                    CloudAccountFormData cloudForm = cloudAccountFormService.findById(id)
                            .orElseThrow(() -> new RuntimeException("Cloud başvurusu bulunamadı"));
                    cloudForm.setStatus(true);
                    cloudForm.setApplicationStatus(ApplicationStatusEnum.APPROVED);
                    cloudForm.setApprovalDate(LocalDateTime.now());
                    cloudAccountFormService.saveCloudAccountFormData(cloudForm);
                    return ResponseEntity.ok("Cloud başvurusu onaylandı");
                default:
                    return ResponseEntity.badRequest().body("Geçersiz form tipi");
            }
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

    @GetMapping("/pending-applications")
    public String getPendingApplications(Model model) {
        // Mail başvuruları
        List<MailFormData> pendingMailForms = mailFormService.findByDurum("PENDING");
        model.addAttribute("pendingMailForms", pendingMailForms);

        // Eduroam başvuruları
        List<EduroamFormData> pendingEduroamForms = eduroamFormService.findByDurum("PENDING");
        model.addAttribute("pendingEduroamForms", pendingEduroamForms);

        // IP-MAC başvuruları
        List<IpMacFormData> pendingIpMacForms = ipMacFormService.findByDurum("PENDING");
        model.addAttribute("pendingIpMacForms", pendingIpMacForms);

        // VPN başvuruları
        List<VpnFormData> pendingVpnForms = vpnFormService.findByDurum("PENDING");
        model.addAttribute("pendingVpnForms", pendingVpnForms);

        return "fragments/pending-applications :: content";
    }

    @GetMapping("/api/applications")
    @ResponseBody
    public List<MailFormData> getMailApplicationsByStatus(@RequestParam String status) {
        List<MailFormData> applications;
        
        switch (status.toLowerCase()) {
            case "pending":
                applications = mailFormService.findByDurum("PENDING");
                break;
            case "approved":
                applications = mailFormService.findByDurum("APPROVED");
                break;
            case "rejected":
                applications = mailFormService.findByDurum("REJECTED");
                break;
            default:
                applications = mailFormService.getAllMailForms();
        }
        
        return applications;
    }
    
    public List<MailFormData> getAllForms() {
        return mailFormService.getAllMailForms();
    }

    @PostMapping("/api/applications/{id}/approve")
    @ResponseBody
    public ResponseEntity<String> approveApplication(@PathVariable Long id) {
        try {
            formService.approveApplication(id);
            return ResponseEntity.ok("Application approved successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("Error approving application: " + e.getMessage());
        }
    }

    @PostMapping("/api/applications/{id}/reject")
    @ResponseBody
    public ResponseEntity<String> rejectApplication(@PathVariable Long id) {
        try {
            formService.rejectApplication(id);
            return ResponseEntity.ok("Application rejected successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("Error rejecting application: " + e.getMessage());
        }
    }

    @GetMapping("/api/applications/{id}")
    @ResponseBody
    public ResponseEntity<BaseFormData> getApplicationDetails(@PathVariable Long id) {
        try {
            BaseFormData application = formService.getApplicationById(id);
            return ResponseEntity.ok(application);
        } catch (Exception e) {
            return ResponseEntity.status(404).build();
        }
    }

    @GetMapping("/applications/{status}")
    @ResponseBody
    public ResponseEntity<List<MailFormData>> getApplicationsByStatus(@PathVariable String status) {
        List<MailFormData> applications;
        
        switch (status.toLowerCase()) {
            case "pending":
                applications = mailFormService.findByDurum("PENDING");
                break;
            case "approved":
                applications = mailFormService.findByDurum("APPROVED");
                break;
            case "rejected":
                applications = mailFormService.findByDurum("REJECTED");
                break;
            default:
                applications = mailFormService.getAllMailForms();
        }
        
        return ResponseEntity.ok(applications);
    }

    /**
     * Returns a list of pending mail applications in text format.
     * Format: tcKimlikNo#ogrenciNo#ad#soyad#fakülte#bölüm#gsm1#e-posta1
     * 
     * @return String containing all pending applications in the specified format
     */
    @GetMapping("/mail/pending-applications-text")
    @ResponseBody
    public ResponseEntity<String> getPendingMailApplicationsAsText() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.findByidentityNumber(auth.getName());
        
        // Check if user is authenticated and has permission
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
        }
        
        // Check if user has permission to view mail tab
        Map<String, Boolean> tabPermissions = adminTabPermissionService.getTabPermissions(currentUser.getId());
        if (!tabPermissions.getOrDefault("mail", false)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Permission denied");
        }
        
        // Get pending applications as text
        String textData = mailFormService.getPendingApplicationsAsText();
        
        // Format the text for proper display in browser with UTF-8 encoding
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/plain;charset=UTF-8"));
        
        return ResponseEntity.ok()
            .headers(headers)
            .body(textData);
    }

    /**
     * Returns a list of pending eduroam applications in text format.
     * Format: tcKimlikNo#ogrenciNo#ad#soyad#fakülte#bölüm#gsm1#e-posta1
     * 
     * @return String containing all pending applications in the specified format
     */
    @GetMapping("/eduroam/pending-applications-text")
    @ResponseBody
    public ResponseEntity<String> getPendingEduroamApplicationsAsText() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.findByidentityNumber(auth.getName());
        
        // Check if user is authenticated and has permission
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
        }
        
        // Check if user has permission to view eduroam tab
        Map<String, Boolean> tabPermissions = adminTabPermissionService.getTabPermissions(currentUser.getId());
        if (!tabPermissions.getOrDefault("eduroam", false)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Permission denied");
        }
        
        // Get pending applications as text
        String textData = eduroamFormService.getPendingApplicationsAsText();
        
        // Format the text for proper display in browser with UTF-8 encoding
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/plain;charset=UTF-8"));
        
        return ResponseEntity.ok()
            .headers(headers)
            .body(textData);
    }
}