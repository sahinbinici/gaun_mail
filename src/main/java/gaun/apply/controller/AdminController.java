package gaun.apply.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gaun.apply.service.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import gaun.apply.dto.StudentDto;
import gaun.apply.dto.UserDto;
import gaun.apply.entity.EduroamFormData;
import gaun.apply.entity.MailFormData;
import gaun.apply.entity.Staff;
import gaun.apply.entity.form.IpMacFormData;
import gaun.apply.entity.form.WirelessNetworkFormData;
import gaun.apply.entity.user.User;
import gaun.apply.repository.EduroamFormRepository;
import gaun.apply.repository.MailFormRepository;
import gaun.apply.repository.form.IpMacFormRepository;
import gaun.apply.repository.form.WirelessNetworkFormRepository;
import gaun.apply.service.StudentService;
import gaun.apply.service.UserService;

@Controller
public class AdminController {
    private final UserService userService;
    private final MailFormRepository mailFormRepository;
    
    @Autowired
    private EduroamFormRepository eduroamFormRepository;

    @Autowired
    private StudentService studentService;

    @Autowired
    private StaffService staffService;

    @Autowired
    private WirelessNetworkFormRepository wirelessNetworkFormRepository;

    @Autowired
    private IpMacFormRepository ipMacFormRepository;

    public AdminController(UserService userService, MailFormRepository mailFormRepository) {
        this.userService = userService;
        this.mailFormRepository = mailFormRepository;
    }

    @GetMapping("/admin")
    public String showAdminPanel(Model model) {
        // Mail istatistikleri
        long totalMails = mailFormRepository.count();
        long pendingMails = mailFormRepository.countByStatus(false);
        
        Map<String, Long> mailStats = new HashMap<>();
        mailStats.put("total", totalMails);
        mailStats.put("pending", pendingMails);
        
        // Eduroam istatistikleri
        long totalEduroam = eduroamFormRepository.count();
        long pendingEduroam = eduroamFormRepository.countByStatus(false);
        
        Map<String, Long> eduroamStats = new HashMap<>();
        eduroamStats.put("total", totalEduroam);
        eduroamStats.put("pending", pendingEduroam);
        
        // Kullanıcı istatistikleri
        long totalUsers = userService.countUsers();
        long activeUsers = userService.countActiveUsers();
        
        Map<String, Long> userStats = new HashMap<>();
        userStats.put("total", totalUsers);
        userStats.put("active", activeUsers);
        
        // Yeni form istatistikleri
        Map<String, Long> wirelessStats = new HashMap<>();
        wirelessStats.put("total", wirelessNetworkFormRepository.count());
        wirelessStats.put("pending", wirelessNetworkFormRepository.countByStatus(false));
        
        Map<String, Long> ipMacStats = new HashMap<>();
        ipMacStats.put("total", ipMacFormRepository.count());
        ipMacStats.put("pending", ipMacFormRepository.countByStatus(false));
        
        // Son başvurular
        List<MailFormData> recentMailForms = mailFormRepository.findTop10ByOrderByApplyDateDesc();
        List<EduroamFormData> recentEduroamForms = eduroamFormRepository.findTop10ByOrderByApplyDateDesc();
        List<WirelessNetworkFormData> recentWirelessForms = wirelessNetworkFormRepository.findTop10ByOrderByApplyDateDesc();
        List<IpMacFormData> recentIpMacForms = ipMacFormRepository.findTop10ByOrderByApplyDateDesc();
        
        model.addAttribute("mailStats", mailStats);
        model.addAttribute("eduroamStats", eduroamStats);
        model.addAttribute("userStats", userStats);
        model.addAttribute("wirelessStats", wirelessStats);
        model.addAttribute("ipMacStats", ipMacStats);
        model.addAttribute("recentMailForms", recentMailForms);
        model.addAttribute("recentEduroamForms", recentEduroamForms);
        model.addAttribute("recentWirelessForms", recentWirelessForms);
        model.addAttribute("recentIpMacForms", recentIpMacForms);
        
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
        MailFormData mailForm = mailFormRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mail başvurusu bulunamadı"));
        mailForm.setStatus(true);
        mailFormRepository.save(mailForm);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/eduroam/activate/{id}")
    @ResponseBody
    public ResponseEntity<?> activateEduroamForm(@PathVariable Long id) {
        EduroamFormData eduroamForm = eduroamFormRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Eduroam başvurusu bulunamadı"));
        eduroamForm.setStatus(true);
        eduroamFormRepository.save(eduroamForm);
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
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Kullanıcı bilgileri alınamadı");
        }
    }
} 