package gaun.apply.application.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gaun.apply.application.dto.DashboardDataDTO;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import gaun.apply.domain.user.entity.Staff;
import gaun.apply.domain.eduroam.entity.EduroamFormData;
import gaun.apply.domain.mail.entity.MailFormData;
import gaun.apply.domain.user.entity.User;
import gaun.apply.domain.common.BaseFormData;
import gaun.apply.application.dto.StudentDto;
import gaun.apply.domain.user.service.StaffService;
import gaun.apply.domain.user.service.StudentService;
import gaun.apply.domain.user.service.UserService;
import gaun.apply.domain.common.FormService;
import gaun.apply.domain.eduroam.service.EduroamFormService;
import gaun.apply.domain.mail.service.MailFormService;
import gaun.apply.infrastructure.service.SmsService;
import gaun.apply.infrastructure.service.AdminTabPermissionService;
import gaun.apply.common.enums.ApplicationStatusEnum;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import static gaun.apply.domain.eduroam.service.EduroamFormService.getEduroamStringBuilder;
import static gaun.apply.domain.mail.service.MailFormService.getMailStringBuilder;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;
    private final FormService formService;
    private final StudentService studentService;
    private final StaffService staffService;
    private final MailFormService mailFormService;
    private final EduroamFormService eduroamFormService;
    private final AdminTabPermissionService adminTabPermissionService;
    private final SmsService smsService;

    public AdminController(UserService userService,
                           FormService formService,
                           StudentService studentService,
                           StaffService staffService,
                           MailFormService mailFormService,
                           EduroamFormService eduroamFormService,
                           AdminTabPermissionService adminTabPermissionService,
                           SmsService smsService) {
        this.userService = userService;
        this.formService = formService;
        this.studentService = studentService;
        this.staffService = staffService;
        this.mailFormService = mailFormService;
        this.eduroamFormService = eduroamFormService;
        this.adminTabPermissionService = adminTabPermissionService;
        this.smsService = smsService;
    }

    @GetMapping({"", "/"})
    public String showAdminDashboard(Model model, HttpServletRequest request, HttpSession session,
                                     @RequestParam(required = false) String filter,
                                     @RequestParam(required = false) String tcKimlikNo,
                                     @RequestParam(required = false) String status) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }

        setupUserAttributes(model, currentUser);
        Map<String, Boolean> tabPermissions = setupTabPermissions(model, currentUser);
        String activeTab = determineActiveTab(session, request, tabPermissions);
        model.addAttribute("activeTab", activeTab);

        prepareDashboardData(model, filter, tcKimlikNo, status);

        return "admin";
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userService.findByidentityNumber(auth.getName());
    }

    private void setupUserAttributes(Model model, User currentUser) {
        model.addAttribute("user", currentUser);
        model.addAttribute("userType", "ADMIN");
    }

    private Map<String, Boolean> setupTabPermissions(Model model, User currentUser) {
        Map<String, Boolean> tabPermissions = adminTabPermissionService.getTabPermissions(currentUser.getId());

        model.addAttribute("hasMailTabPermission", tabPermissions.getOrDefault("mail", false));
        model.addAttribute("hasEduroamTabPermission", tabPermissions.getOrDefault("eduroam", false));
        model.addAttribute("tabPermissions", tabPermissions);

        return tabPermissions;
    }

    private String determineActiveTab(HttpSession session, HttpServletRequest request, Map<String, Boolean> tabPermissions) {
        String activeTab = (String) session.getAttribute("activeTab");
        if (activeTab == null) {
            activeTab = request.getParameter("tab");
            if (activeTab == null) {
                activeTab = tabPermissions.getOrDefault("mail", false) ? "mail" : "eduroam";
            }
            session.setAttribute("activeTab", activeTab);
        }
        return activeTab;
    }

    private void prepareDashboardData(Model model, String filter, String tcKimlikNo, String status) {
        DashboardDataDTO dashboardData = formService.prepareDashboardData(filter, tcKimlikNo, status);

        model.addAttribute("mailForms", dashboardData.getMailForms());
        model.addAttribute("eduroamForms", dashboardData.getEduroamForms());
        model.addAttribute("mailStats", dashboardData.getMailStats());
        model.addAttribute("eduroamStats", dashboardData.getEduroamStats());
        model.addAttribute("userStats", dashboardData.getUserStats());
    }

    /**
     * Provides default user statistics
     * Bu metodu UserService sınıfında ilgili metodlar eklendikten sonra gerçek verilerle güncelleyebilirsiniz
     */
    private Map<String, Long> getUserStats() {
        Map<String, Long> stats = new HashMap<>();
        
        // Varsayılan değerler atıyoruz
        stats.put("total", 0L);
        stats.put("active", 0L);
        
        return stats;
    }

    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("users", userService.findAllUsers());
        return "admin/users";
    }
    @PostMapping("/mail/activate/{id}")
    @ResponseBody
    public ResponseEntity<?> activateMailForm(@PathVariable Long id) {
        try {
            MailFormData mailForm = mailFormService.findMailFormById(id)
                    .orElseThrow(() -> new RuntimeException("Mail başvurusu bulunamadı"));

            mailForm.setStatus(true);
            mailForm.setApplicationStatus(ApplicationStatusEnum.APPROVED);
            mailForm.setApprovalDate(LocalDateTime.now());

            boolean isStudent = mailForm.getOgrenciNo() != null;
            MailFormData existingMailForm = mailFormService.findByTcKimlikNo(mailForm.getTcKimlikNo());

            /*
            String emailAddress = isStudent
                    ? studentService.createEmailAddress(mailForm.getOgrenciNo())
                    : staffService.createEmailAddress(mailForm.getTcKimlikNo()); */

            mailForm.setEmail(existingMailForm.getEmail());
            mailFormService.save(mailForm);

            sendApprovalSms(mailForm, existingMailForm.getEmail(), isStudent);

            return ResponseEntity.ok().body("Mail başvurusu onaylandı ");
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(" Mail başvurusu onaylanamadı: " + e.getMessage());
        }
    }

    private void sendApprovalSms(MailFormData mailForm, String emailAddress, boolean isStudent) {
        try {
            String message = "GAÜN E-posta başvurunuz onaylanmıştır. E-posta adresiniz: " +
                    emailAddress + " Şifreniz: " + mailForm.getPassword();

            if (isStudent) {
                StudentDto student = studentService.findByOgrenciNo(mailForm.getOgrenciNo());
                if (student != null && student.getGsm1() != null && !student.getGsm1().isEmpty()) {
                    smsService.sendSms(new String[]{student.getGsm1()}, message);
                }
            } else {
                String tcKimlikNo = mailForm.getTcKimlikNo();
                if (tcKimlikNo != null && !tcKimlikNo.isEmpty()) {
                    Staff staff = staffService.findByTcKimlikNo(tcKimlikNo);
                    if (staff != null && staff.getGsm() != null && !staff.getGsm().isEmpty()) {
                        smsService.sendSms(new String[]{staff.getGsm()}, message);
                    }
                }
            }
        } catch (Exception ex) {
            System.err.println("Mail başvurusu onay SMS'i gönderiminde hata: " + ex.getMessage());
        }
    }
/*
    @PostMapping("/mail/activate/{id}")
    @ResponseBody
    public ResponseEntity<?> activateMailForm(@PathVariable Long id) {
        try {
            MailFormData mailForm = mailFormService.findMailFormById(id)
                    .orElseThrow(() -> new RuntimeException("Mail başvurusu bulunamadı"));

            mailForm.setStatus(true);
            mailForm.setApplicationStatus(ApplicationStatusEnum.APPROVED); // Fix: Set status enum to APPROVED
            mailForm.setApprovalDate(LocalDateTime.now());

            // Öğrenci mi personel mi kontrolü (öğrenci ID'si 12 haneli)
            boolean isStudent = mailForm.getOgrenciNo() != null;//username.length() == 12 && username.matches("\\d+");
            
            // Doğru servisi kullanarak e-posta adresi oluşturma
            String emailAddress;
            if (isStudent) {
                // Öğrenci için email oluştur - DTO'dan TC Kimlik No kullan
                emailAddress = studentService.createEmailAddress(mailForm.getOgrenciNo());
            } else {
                // Personel için email oluştur
                emailAddress = staffService.createEmailAddress(mailForm.getTcKimlikNo());
            }
            
            mailForm.setEmail(emailAddress);
            mailFormService.save(mailForm);
            
            // Onay SMS'i gönder
            try {
                if (isStudent) {
                    // Öğrenci için SMS gönder
                    StudentDto student = studentService.findByOgrenciNo(mailForm.getOgrenciNo());
                    if (student != null && student.getGsm1() != null && !student.getGsm1().isEmpty()) {
                        smsService.sendSms(new String[]{student.getGsm1()}, 
                                "GAÜN E-posta başvurunuz onaylanmıştır. E-posta adresiniz: " + emailAddress + "Şifreniz: " + mailForm.getPassword());
                    }
                } else {
                    // Personel için SMS gönder
                    String tcKimlikNo = mailForm.getTcKimlikNo();
                    if (tcKimlikNo != null && !tcKimlikNo.isEmpty()) {
                        Staff staff = staffService.findByTcKimlikNo(tcKimlikNo);
                        if (staff != null && staff.getGsm() != null && !staff.getGsm().isEmpty()) {
                            smsService.sendSms(new String[]{staff.getGsm()}, 
                                    "GAÜN E-posta başvurunuz onaylanmıştır. E-posta adresiniz: " + emailAddress + "Şifreniz: " + mailForm.getPassword());
                        }
                    }
                }
            } catch (Exception ex) {
                System.err.println("Mail başvurusu onay SMS'i gönderiminde hata: " + ex.getMessage());
                // SMS gönderimindeki hata başvuru onayını etkilememeli
            }
            
            return ResponseEntity.ok().body("Mail başvurusu onaylandı");
        } catch (Exception e) {
            // Error handling in form approval methods - Fix from memory
            return ResponseEntity.badRequest()
                    .body("Mail başvurusu onaylanamadı: " + e.getMessage());
        }
    }
    */
    @PostMapping("/eduroam/activate/{id}")
    @ResponseBody
    public ResponseEntity<?> activateEduroamForm(@PathVariable Long id, Model model) {
        try {
            EduroamFormData eduroamForm = findAndActivateForm(id);
            sendActivationNotification(eduroamForm);
            return ResponseEntity.ok().body("Eduroam başvurusu onaylandı");
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Eduroam başvurusu onaylanamadı: " + e.getMessage());
        }
    }

    private EduroamFormData findAndActivateForm(Long id) {
        EduroamFormData eduroamForm = eduroamFormService.findById(id)
                .orElseThrow(() -> new RuntimeException("Eduroam başvurusu bulunamadı"));

        eduroamForm.setStatus(true);
        eduroamForm.setApplicationStatus(ApplicationStatusEnum.APPROVED);
        eduroamForm.setApprovalDate(LocalDateTime.now());
        eduroamFormService.saveEduroamFormData(eduroamForm);

        return eduroamForm;
    }

    private void sendActivationNotification(EduroamFormData eduroamForm) {
        try {
            String tcKimlikNo = eduroamForm.getTcKimlikNo();
            boolean isStudent = isStudentApplication(eduroamForm);

            if (isStudent) {
                sendStudentNotification(eduroamForm, tcKimlikNo);
            } else {
                sendStaffNotification(tcKimlikNo);
            }
        } catch (Exception ex) {
            System.err.println("Eduroam başvurusu onay SMS'i gönderiminde hata: " + ex.getMessage());
        }
    }

    private boolean isStudentApplication(EduroamFormData eduroamForm) {
        return eduroamForm.getOgrenciNo() != null;
    }

    private void sendStudentNotification(EduroamFormData eduroamForm, String username) {
        StudentDto student = studentService.findByOgrenciNo(eduroamForm.getOgrenciNo());
        if (student != null && student.getGsm1() != null && !student.getGsm1().isEmpty()) {
            String message = "GAÜN Eduroam başvurunuz onaylanmıştır. Kullanıcı adınız: " + username;
            smsService.sendSms(new String[]{student.getGsm1()}, message);
        }
    }

    private void sendStaffNotification(String tcKimlikNo) {
        if (tcKimlikNo != null && !tcKimlikNo.isEmpty()) {
            Staff staff = staffService.findByTcKimlikNo(tcKimlikNo);
            if (staff != null && staff.getGsm() != null && !staff.getGsm().isEmpty()) {
                String message = "GAÜN Eduroam başvurunuz onaylanmıştır. Kullanıcı adınız: " + tcKimlikNo;
                smsService.sendSms(new String[]{staff.getGsm()}, message);
            } else {
                System.out.println("Personel için telefon numarası bulunamadı: " + tcKimlikNo);
            }
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
            Map<String, Object> response = formService.getUserDetails(username);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
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
            // FormService üzerinden form aktivasyonu
            String successMessage = formService.activateForm(formType, id);
            return ResponseEntity.ok(successMessage);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Form aktivasyonu başarısız: " + e.getMessage());
        }
    }

    @PostMapping("/{formType}/reject/{id}")
    @ResponseBody
    public ResponseEntity<?> rejectForm(@PathVariable String formType, 
                                      @PathVariable Long id,
                                      @RequestParam("reason") String reason) {
        try {
            // Debug bilgisi
            System.out.println("Reddedilen form - Tip: " + formType + ", ID: " + id);
            System.out.println("Red sebebi: " + reason);
            
            // Form tipini düzgün formatlayalım
            String className = "";
            switch (formType) {
                case "mail":
                    className = "MailFormData";
                    break;
                case "eduroam":
                    className = "EduroamFormData";
                    break;
                default:
                    throw new IllegalArgumentException("Geçersiz form tipi: " + formType);
            }

            @SuppressWarnings("unchecked")
            Class<? extends BaseFormData> formClass = 
                (Class<? extends BaseFormData>) Class.forName("gaun.apply.domain." + formType + ".entity." + className);
                
            formService.rejectForm(id, formClass, reason);
            return ResponseEntity.ok().body("Başvuru başarıyla reddedildi");
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

    @GetMapping("/pending-applications")
    public String getPendingApplications(Model model) {
        // Mail başvuruları
        List<MailFormData> pendingMailForms = mailFormService.findByDurum("PENDING");
        model.addAttribute("pendingMailForms", pendingMailForms);

        // Eduroam başvuruları
        List<EduroamFormData> pendingEduroamForms = eduroamFormService.findByDurum("PENDING");
        model.addAttribute("pendingEduroamForms", pendingEduroamForms);

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

    @GetMapping("/mail/pending-applications-text")
    @ResponseBody
    public ResponseEntity<String> getPendingMailApplicationsAsText() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.findByidentityNumber(auth.getName());
        
        // Check if user is authenticated and has permission
        if (currentUser == null || !adminTabPermissionService.hasTabPermission(currentUser.getId(), "mail")) {
            return ResponseEntity.status(403)
                    .body("Bu işlem için yetkiniz yok");
        }
        
        List<MailFormData> pendingForms = mailFormService.findByDurum("PENDING");
        if (pendingForms.isEmpty()) {
            return ResponseEntity.ok("Bekleyen başvuru bulunamadı");
        }

        StringBuilder sb = getMailStringBuilder(pendingForms);

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(sb.toString());
    }

    @GetMapping("/eduroam/pending-applications-text")
    @ResponseBody
    public ResponseEntity<String> getPendingEduroamApplicationsAsText() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.findByidentityNumber(auth.getName());
        
        // Check if user is authenticated and has permission
        if (currentUser == null || !adminTabPermissionService.hasTabPermission(currentUser.getId(), "eduroam")) {
            return ResponseEntity.status(403)
                    .body("Bu işlem için yetkiniz yok");
        }
        
        List<EduroamFormData> pendingForms = eduroamFormService.findByDurum("PENDING");
        if (pendingForms.isEmpty()) {
            return ResponseEntity.ok("Bekleyen başvuru bulunamadı");
        }

        StringBuilder sb = getEduroamStringBuilder(pendingForms);

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(sb.toString());
    }
}