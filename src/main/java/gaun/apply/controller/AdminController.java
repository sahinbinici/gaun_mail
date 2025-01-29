package gaun.apply.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
import gaun.apply.entity.form.WirelessNetworkFormData;
import gaun.apply.entity.user.User;
import gaun.apply.repository.form.MailFormRepository;
import gaun.apply.service.StaffService;
import gaun.apply.service.StudentService;
import gaun.apply.service.UserService;
import gaun.apply.service.form.CloudAccountFormService;
import gaun.apply.service.form.EduroamFormService;
import gaun.apply.service.form.FormService;
import gaun.apply.service.form.IpMacFormService;
import gaun.apply.service.form.MailFormService;
import gaun.apply.service.form.VpnFormService;
import gaun.apply.service.form.WirelessNetworkFormService;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class AdminController {
    private final UserService userService;
    private final MailFormRepository mailFormRepository;
    private final FormService formService;
    private final StudentService studentService;
    private final StaffService staffService;
    private final MailFormService mailFormService;
    private final EduroamFormService eduroamFormService;
    private final VpnFormService vpnFormService;
    private final WirelessNetworkFormService wirelessNetworkFormService;
    private final CloudAccountFormService cloudAccountFormService;
    private final IpMacFormService ipMacFormService;

    public AdminController(UserService userService, MailFormRepository mailFormRepository, FormService formService, StudentService studentService, StaffService staffService, MailFormService mailFormService, EduroamFormService eduroamFormService, VpnFormService vpnFormService, WirelessNetworkFormService wirelessNetworkFormService, CloudAccountFormService cloudAccountFormService, IpMacFormService ipMacFormService) {
        this.userService = userService;
        this.mailFormRepository = mailFormRepository;
        this.formService = formService;
        this.studentService = studentService;
        this.staffService = staffService;
        this.mailFormService = mailFormService;
        this.eduroamFormService = eduroamFormService;
        this.vpnFormService = vpnFormService;
        this.wirelessNetworkFormService = wirelessNetworkFormService;
        this.cloudAccountFormService = cloudAccountFormService;
        this.ipMacFormService = ipMacFormService;
    }

    @GetMapping("/admin")
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
        long mailPendingCount = mailFormService.countByStatus(false);//mailFormRepository.countByStatus(false);
        long mailApprovedCount = mailFormService.countByStatus(true);//mailFormRepository.countByStatus(true);
        List<MailFormData> recentMailForms = mailFormService.findTop10ByOrderByApplyDateDesc();//mailFormRepository.findTop10ByOrderByApplyDateDesc();
        Map<String, Long> mailStats = new HashMap<>();
        mailStats.put("pending", mailPendingCount);
        mailStats.put("approved", mailApprovedCount);
        mailStats.put("total", mailPendingCount + mailApprovedCount);
        
        // Eduroam başvuruları
        long eduroamPendingCount = eduroamFormService.countByStatus(false);//eduroamFormRepository.countByStatus(false);
        long eduroamApprovedCount = eduroamFormService.countByStatus(true);//eduroamFormRepository.countByStatus(true);
        List<EduroamFormData> recentEduroamForms = eduroamFormService.findTop10ByOrderByApplyDateDesc();//eduroamFormRepository.findTop10ByOrderByApplyDateDesc();
        Map<String, Long> eduroamStats = new HashMap<>();
        eduroamStats.put("pending", eduroamPendingCount);
        eduroamStats.put("approved", eduroamApprovedCount);
        eduroamStats.put("total", eduroamPendingCount + eduroamApprovedCount);
        
        // Kablosuz ağ başvuruları
        long wirelessPendingCount = wirelessNetworkFormService.countByStatus(false);//wirelessNetworkFormRepository.countByStatus(false);
        long wirelessApprovedCount = wirelessNetworkFormService.countByStatus(true);//wirelessNetworkFormRepository.countByStatus(true);
        List<WirelessNetworkFormData> recentWirelessForms = wirelessNetworkFormService.findTop10ByOrderByApplyDateDesc();//wirelessNetworkFormRepository.findTop10ByOrderByApplyDateDesc();
        Map<String, Long> wirelessStats = new HashMap<>();
        wirelessStats.put("pending", wirelessPendingCount);
        wirelessStats.put("approved", wirelessApprovedCount);
        wirelessStats.put("total", wirelessPendingCount + wirelessApprovedCount);
        
        // IP-MAC başvuruları
        long ipMacPendingCount = ipMacFormService.countByStatus(false);//ipMacFormRepository.countByStatus(false);
        long ipMacApprovedCount = ipMacFormService.countByStatus(true);//ipMacFormRepository.countByStatus(true);
        List<IpMacFormData> recentIpMacForms = ipMacFormService.findTop10ByOrderByApplyDateDesc();//ipMacFormRepository.findTop10ByOrderByApplyDateDesc();
        Map<String, Long> ipMacStats = new HashMap<>();
        ipMacStats.put("pending", ipMacPendingCount);
        ipMacStats.put("approved", ipMacApprovedCount);
        ipMacStats.put("total", ipMacPendingCount + ipMacApprovedCount);
        
        // GAUN Bulut başvuruları
        long cloudPendingCount = cloudAccountFormService.countByStatus(false);//cloudAccountFormRepository.countByStatus(false);
        long cloudApprovedCount = cloudAccountFormService.countByStatus(true);//cloudAccountFormRepository.countByStatus(true);
        List<CloudAccountFormData> recentCloudForms = cloudAccountFormService.findTop10ByOrderByApplyDateDesc();//cloudAccountFormRepository.findTop10ByOrderByApplyDateDesc();
        Map<String, Long> cloudStats = new HashMap<>();
        cloudStats.put("pending", cloudPendingCount);
        cloudStats.put("approved", cloudApprovedCount);
        cloudStats.put("total", cloudPendingCount + cloudApprovedCount);
        
        // VPN başvuruları
        long vpnPendingCount = vpnFormService.countByStatus(false);//vpnFormRepository.countByStatus(false);
        long vpnApprovedCount = vpnFormService.countByStatus(true);//vpnFormRepository.countByStatus(true);
        List<VpnFormData> recentVpnForms = vpnFormService.findTop10ByOrderByApplyDateDesc();//vpnFormRepository.findTop10ByOrderByApplyDateDesc();
        Map<String, Long> vpnStats = new HashMap<>();
        vpnStats.put("pending", vpnPendingCount);
        vpnStats.put("approved", vpnApprovedCount);
        vpnStats.put("total", vpnPendingCount + vpnApprovedCount);
        
        // İstatistikleri model'e ekle
        model.addAttribute("mailStats", mailStats);
        model.addAttribute("eduroamStats", eduroamStats);
        model.addAttribute("wirelessStats", wirelessStats);
        model.addAttribute("ipMacStats", ipMacStats);
        model.addAttribute("cloudStats", cloudStats);
        model.addAttribute("vpnStats", vpnStats);
        
        // Son başvuruları model'e ekle
        model.addAttribute("recentMailForms", recentMailForms);
        model.addAttribute("recentEduroamForms", recentEduroamForms);
        model.addAttribute("recentWirelessForms", recentWirelessForms);
        model.addAttribute("recentIpMacForms", recentIpMacForms);
        model.addAttribute("recentCloudForms", recentCloudForms);
        model.addAttribute("recentVpnForms", recentVpnForms);
        
        // Başvuru sayılarını model'e ekle
        model.addAttribute("hasMailForms", !recentMailForms.isEmpty());
        model.addAttribute("hasEduroamForms", !recentEduroamForms.isEmpty());
        model.addAttribute("hasWirelessForms", !recentWirelessForms.isEmpty());
        model.addAttribute("hasIpMacForms", !recentIpMacForms.isEmpty());
        model.addAttribute("hasCloudForms", !recentCloudForms.isEmpty());
        model.addAttribute("hasVpnForms", !recentVpnForms.isEmpty());
        
        // CSRF token'ı ekle
        CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (csrf != null) {
            model.addAttribute("_csrf", csrf);
        }
        
        return "admin";
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

    @GetMapping("/api/user-details/{username}")
    @ResponseBody
    public ResponseEntity<?> getUserDetails(@PathVariable String username) {
        try {
            User user = userService.findByidentityNumber(username);
            
            if (user == null) {
                return ResponseEntity.notFound().build();
            }

            Map<String, Object> response = new HashMap<>();
            
            // Kullanıcının rolüne göre öğrenci veya personel bilgilerini getir
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
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Kullanıcı bilgileri alınamadı: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
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

    @PostMapping("/wireless/activate/{id}")
    @ResponseBody
    public ResponseEntity<?> activateWirelessForm(@PathVariable Long id) {
        WirelessNetworkFormData wirelessForm = wirelessNetworkFormService.findById(id)//wirelessNetworkFormRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kablosuz ağ başvurusu bulunamadı"));
        wirelessForm.setStatus(true);
        wirelessForm.setApprovalDate(LocalDateTime.now());
        wirelessNetworkFormService.saveWirelessNetworkFormData(wirelessForm);//wirelessNetworkFormRepository.save(wirelessForm);
        return ResponseEntity.ok().build();
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
        VpnFormData vpnForm = vpnFormService.findById(id)
                .orElseThrow(() -> new RuntimeException("VPN başvurusu bulunamadı"));
        vpnForm.setStatus(true);
        vpnForm.setApprovalDate(LocalDateTime.now());
        vpnFormService.saveVpnFormData(vpnForm);
        return ResponseEntity.ok().build();
    }
} 