package gaun.apply.application.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        // Get current user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.findByidentityNumber(auth.getName());
        
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("user", currentUser);
        model.addAttribute("userType", "ADMIN");
        
        // Get permission settings
        boolean hasMailTabPermission = adminTabPermissionService.hasTabPermission(currentUser.getId(), "mail");
        boolean hasEduroamTabPermission = adminTabPermissionService.hasTabPermission(currentUser.getId(), "eduroam");
        
        model.addAttribute("hasMailTabPermission", hasMailTabPermission);
        model.addAttribute("hasEduroamTabPermission", hasEduroamTabPermission);
        
        // Create tabPermissions map for template
        Map<String, Boolean> tabPermissions = new HashMap<>();
        tabPermissions.put("mail", hasMailTabPermission);
        tabPermissions.put("eduroam", hasEduroamTabPermission);
        model.addAttribute("tabPermissions", tabPermissions);
        
        // Get active tab from session or request
        String activeTab = (String) session.getAttribute("activeTab");
        if (activeTab == null) {
            activeTab = request.getParameter("tab");
            if (activeTab == null) {
                activeTab = hasMailTabPermission ? "mail" : "eduroam";
            }
            session.setAttribute("activeTab", activeTab);
        }
        model.addAttribute("activeTab", activeTab);
        
        // Handle form filter based on selected tab
        List<MailFormData> mailForms;
        List<EduroamFormData> eduroamForms;
        
        // Apply filters if specified
        if (tcKimlikNo != null && !tcKimlikNo.isEmpty()) {
            mailForms = mailFormService.findByTcKimlikNo(tcKimlikNo) != null ? 
                    List.of(mailFormService.findByTcKimlikNo(tcKimlikNo)) : 
                    new ArrayList<>();
            
            eduroamForms = eduroamFormService.eduroamFormDataTc(tcKimlikNo) != null ? 
                    List.of(eduroamFormService.eduroamFormDataTc(tcKimlikNo)) : 
                    new ArrayList<>();
        } else if (status != null && !status.isEmpty()) {
            mailForms = mailFormService.findByDurum(status);
            eduroamForms = eduroamFormService.findByDurum(status);
        } else if (filter != null && filter.equals("pending")) {
            mailForms = mailFormService.findByDurum("PENDING");
            eduroamForms = eduroamFormService.findByDurum("PENDING");
        } else if (filter != null && filter.equals("approved")) {
            mailForms = mailFormService.findByDurum("APPROVED");
            eduroamForms = eduroamFormService.findByDurum("APPROVED");
        } else if (filter != null && filter.equals("rejected")) {
            mailForms = mailFormService.findByDurum("REJECTED");
            eduroamForms = eduroamFormService.findByDurum("REJECTED");
        } else {
            // Default: show all forms
            mailForms = mailFormService.getAllMailForms();
            eduroamForms = eduroamFormService.getAllEduroamForms();
        }
        
        // Add form statistics with null-safety
        Map<String, Long> mailStats = getFormStats(mailForms != null ? mailForms : new ArrayList<>());
        Map<String, Long> eduroamStats = getFormStats(eduroamForms != null ? eduroamForms : new ArrayList<>());
        
        // Add user statistics
        Map<String, Long> userStats = getUserStats();
        
        model.addAttribute("mailStats", mailStats);
        model.addAttribute("eduroamStats", eduroamStats);
        model.addAttribute("userStats", userStats);
        
        // Add form data to model
        model.addAttribute("mailForms", mailForms);
        model.addAttribute("eduroamForms", eduroamForms);
        
        return "admin";
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
    
    /**
     * Calculates statistics for form data
     */
    private Map<String, Long> getFormStats(List<? extends BaseFormData> forms) {
        Map<String, Long> stats = new HashMap<>();
        
        if (forms == null || forms.isEmpty()) {
            stats.put("total", 0L);
            stats.put("pending", 0L);
            stats.put("approved", 0L);
            stats.put("rejected", 0L);
            return stats;
        }
        
        long total = forms.size();
        long pending = forms.stream()
                .filter(form -> "PENDING".equals(form.getApplicationStatus().name()))
                .count();
        long approved = forms.stream()
                .filter(form -> "APPROVED".equals(form.getApplicationStatus().name()))
                .count();
        long rejected = forms.stream()
                .filter(form -> "REJECTED".equals(form.getApplicationStatus().name()))
                .count();
        
        stats.put("total", total);
        stats.put("pending", pending);
        stats.put("approved", approved);
        stats.put("rejected", rejected);
        
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
                                "GAÜN E-posta başvurunuz onaylanmıştır. E-posta adresiniz: " + emailAddress);
                        System.out.println("Öğrenci " + student.getAd() + " " + student.getSoyad() + " için onay SMS'i gönderildi");
                    }
                } else {
                    // Personel için SMS gönder
                    String tcKimlikNo = mailForm.getTcKimlikNo();
                    if (tcKimlikNo != null && !tcKimlikNo.isEmpty()) {
                        Staff staff = staffService.findByTcKimlikNo(tcKimlikNo);
                        if (staff != null && staff.getGsm() != null && !staff.getGsm().isEmpty()) {
                            smsService.sendSms(new String[]{staff.getGsm()}, 
                                    "GAÜN E-posta başvurunuz onaylanmıştır. E-posta adresiniz: " + emailAddress);
                            System.out.println("Personel " + staff.getAd() + " " + staff.getSoyad() + " için onay SMS'i gönderildi");
                        } else {
                            System.out.println("Personel için telefon numarası bulunamadı: " + tcKimlikNo);
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

    @PostMapping("/eduroam/activate/{id}")
    @ResponseBody
    public ResponseEntity<?> activateEduroamForm(@PathVariable Long id, Model model) {
        try {
            EduroamFormData eduroamForm = eduroamFormService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Eduroam başvurusu bulunamadı"));
            
            eduroamForm.setStatus(true);
            eduroamForm.setApplicationStatus(ApplicationStatusEnum.APPROVED); // Fix: Set status enum to APPROVED
            eduroamForm.setApprovalDate(LocalDateTime.now());
            eduroamFormService.saveEduroamFormData(eduroamForm);
            
            // Eduroam onay SMS'i gönder
            try {
                String username = eduroamForm.getTcKimlikNo();
                String tcKimlikNo = eduroamForm.getTcKimlikNo();
                
                // Öğrenci mi personel mi kontrolü (öğrenci ID'si 12 haneli)
                boolean isStudent = eduroamForm.getOgrenciNo()!=null;//username != null && username.length() == 12 && username.matches("\\d+");
                
                if (isStudent) {
                    // Öğrenci için SMS gönder
                    StudentDto student = studentService.findByOgrenciNo(eduroamForm.getOgrenciNo());
                    if (student != null && student.getGsm1() != null && !student.getGsm1().isEmpty()) {
                        smsService.sendSms(new String[]{student.getGsm1()}, 
                                "GAÜN Eduroam başvurunuz onaylanmıştır. Kullanıcı adınız: " + username);
                        System.out.println("Öğrenci " + student.getAd() + " " + student.getSoyad() + " için Eduroam onay SMS'i gönderildi");
                    }
                } else {
                    // Personel için SMS gönder
                    if (tcKimlikNo != null && !tcKimlikNo.isEmpty()) {
                        Staff staff = staffService.findByTcKimlikNo(tcKimlikNo);
                        if (staff != null && staff.getGsm() != null && !staff.getGsm().isEmpty()) {
                            smsService.sendSms(new String[]{staff.getGsm()}, 
                                    "GAÜN Eduroam başvurunuz onaylanmıştır. Kullanıcı adınız: " + tcKimlikNo);
                            System.out.println("Personel " + staff.getAd() + " " + staff.getSoyad() + " için Eduroam onay SMS'i gönderildi");
                        } else {
                            System.out.println("Personel için telefon numarası bulunamadı: " + tcKimlikNo);
                        }
                    }
                }
            } catch (Exception ex) {
                System.err.println("Eduroam başvurusu onay SMS'i gönderiminde hata: " + ex.getMessage());
                // SMS gönderimindeki hata başvuru onayını etkilememeli
            }
            
            return ResponseEntity.ok().body("Eduroam başvurusu onaylandı");
        } catch (Exception e) {
            // Error handling in form approval methods - Fix from memory
            return ResponseEntity.badRequest()
                    .body("Eduroam başvurusu onaylanamadı: " + e.getMessage());
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
            Map<String, Object> response = new HashMap<>();
            
            User user = userService.findByTcKimlikNo(username);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }
            
            // Temel kullanıcı bilgilerini ekle
            response.put("username", user.getIdentityNumber());
            response.put("tcKimlikNo", user.getIdentityNumber());
            
            // Kullanıcı türünü belirle (öğrenci ID'si 12 haneli sayı olmalı)
            boolean isStudent = user.getIdentityNumber().length() == 12;
            response.put("userType", isStudent ? "student" : "staff");
            
            // Kullanıcı türüne göre ek bilgileri getir
            if (isStudent) {
                // Öğrenci bilgilerini ekle
                try {
                    StudentDto studentDto = studentService.findByOgrenciNo(user.getIdentityNumber());
                    if (studentDto != null) {
                        response.put("tcKimlikNo", studentDto.getTcKimlikNo());
                        response.put("ogrenciNo", studentDto.getOgrenciNo());
                        response.put("ad", studentDto.getAd());
                        response.put("soyad", studentDto.getSoyad());
                        response.put("gsm1", studentDto.getGsm1());
                        response.put("fakulte", studentDto.getFakKod());
                        response.put("bolum", studentDto.getBolumAd());
                        response.put("program", studentDto.getProgramAd());
                        response.put("egitimDerecesi", studentDto.getEgitimDerecesi());
                    }
                } catch (Exception ex) {
                    // Öğrenci bilgileri alınamadıysa hata mesajı ekle
                    response.put("error", "Öğrenci bilgileri alınamadı: " + ex.getMessage());
                }
            } else {
                // Personel bilgilerini ekle
                try {
                    Staff staff = staffService.findByTcKimlikNo(user.getIdentityNumber());
                    if (staff != null) {
                        response.put("tcKimlikNo", staff.getTcKimlikNo());
                        response.put("ad", staff.getAd());
                        response.put("soyad", staff.getSoyad());
                        response.put("gsm", staff.getGsm());
                        response.put("birim", staff.getCalistigiBirim()); // Doğru alan adı: calistigiBirim
                        response.put("unvan", staff.getUnvan());
                    }
                } catch (Exception ex) {
                    // Personel bilgileri alınamadıysa hata mesajı ekle
                    response.put("error", "Personel bilgileri alınamadı: " + ex.getMessage());
                }
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
                    mailForm.setApplicationStatus(ApplicationStatusEnum.APPROVED); // Fix: Set status enum to APPROVED
                    mailForm.setApprovalDate(LocalDateTime.now());

                    // Öğrenci mi personel mi kontrolü (öğrenci ID'si 12 haneli)
                    boolean isStudent = mailForm.getOgrenciNo()!=null;//username.length() == 12 && username.matches("\\d+");
                    
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
                    return ResponseEntity.ok("Mail başvurusu onaylandı");
                    
                case "eduroam":
                    EduroamFormData eduroamForm = eduroamFormService.findById(id)
                            .orElseThrow(() -> new RuntimeException("Eduroam başvurusu bulunamadı"));
                    eduroamForm.setStatus(true);
                    eduroamForm.setApplicationStatus(ApplicationStatusEnum.APPROVED); // Fix: Set status enum to APPROVED
                    eduroamForm.setApprovalDate(LocalDateTime.now());
                    eduroamFormService.saveEduroamFormData(eduroamForm);
                    return ResponseEntity.ok("Eduroam başvurusu onaylandı");
                default:
                    return ResponseEntity.badRequest().body("Geçersiz form tipi");
            }
        } catch (Exception e) {
            // Error handling in form approval methods - Fix from memory
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
        if (currentUser == null || !adminTabPermissionService.hasTabPermission(currentUser.getId(), "mail")) {
            return ResponseEntity.status(403)
                    .body("Bu işlem için yetkiniz yok");
        }
        
        List<MailFormData> pendingForms = mailFormService.findByDurum("PENDING");
        if (pendingForms.isEmpty()) {
            return ResponseEntity.ok("Bekleyen başvuru bulunamadı");
        }
        
        StringBuilder sb = new StringBuilder();
        for (MailFormData form : pendingForms) {
            sb.append(form.getTcKimlikNo()).append("#")
              .append(form.getOgrenciNo()!=null ? form.getOgrenciNo() : form.getSicil()).append("#")
              .append(form.getAd()).append("#")
              .append(form.getSoyad()).append("#")
              .append(form.getOgrenciNo() !=null ? form.getFakulte() : form.getCalistigiBirim()).append("#")
              .append(form.getOgrenciNo()!=null ? form.getBolum() : form.getUnvan()).append("#")
              .append(form.getGsm1()).append("#")
              .append(form.getEmail()+"@gantep.edu.tr").append("#")
                    .append(form.getSicil()!=null ? form.getDogumTarihi() : "")
                    .append("\n").append(System.lineSeparator());
        }
        
        return ResponseEntity.ok(sb.toString());
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
        if (currentUser == null || !adminTabPermissionService.hasTabPermission(currentUser.getId(), "eduroam")) {
            return ResponseEntity.status(403)
                    .body("Bu işlem için yetkiniz yok");
        }
        
        List<EduroamFormData> pendingForms = eduroamFormService.findByDurum("PENDING");
        if (pendingForms.isEmpty()) {
            return ResponseEntity.ok("Bekleyen başvuru bulunamadı");
        }
        
        StringBuilder sb = new StringBuilder();
        for (EduroamFormData form : pendingForms) {
            sb.append(form.getTcKimlikNo()).append("#")
              .append(form.getOgrenciNo()!=null ? form.getOgrenciNo() : form.getSicilNo()).append("#")
              .append(form.getAd()).append("#")
              .append(form.getSoyad()).append("#")
              .append(form.getOgrenciNo()!=null ? form.getFakulte() : form.getCalistigiBirim()).append("#")
              .append(form.getOgrenciNo()!= null ? form.getBolum() : form.getUnvan()).append("#")
              .append(form.getGsm1()).append("#")
              .append(form.getEmail()).append("#")
                    .append(form.getOgrenciNo()==null ? form.getDogumTarihi() : "").append("\n").append(System.lineSeparator());
        }
        
        return ResponseEntity.ok(sb.toString());
    }
}
