package gaun.apply.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import gaun.apply.dto.EduroamFormDto;
import gaun.apply.dto.MailFormDto;
import gaun.apply.entity.EduroamFormData;
import gaun.apply.entity.MailFormData;
import gaun.apply.entity.Staff;
import gaun.apply.entity.user.User;
import gaun.apply.repository.EduroamFormRepository;
import gaun.apply.repository.MailFormRepository;
import gaun.apply.service.StaffService;
import gaun.apply.service.UserService;

@Controller
@RequestMapping("/staff")
public class StaffUserController {
    private final UserService userService;
    private final StaffService staffService;
    private final MailFormRepository mailFormRepository;
    private final EduroamFormRepository eduroamFormRepository;

    public StaffUserController(UserService userService, 
                             StaffService staffService,
                             MailFormRepository mailFormRepository,
                             EduroamFormRepository eduroamFormRepository) {
        this.userService = userService;
        this.staffService = staffService;
        this.mailFormRepository = mailFormRepository;
        this.eduroamFormRepository = eduroamFormRepository;
    }

    @GetMapping("/wireless-network")
    public String showWirelessNetworkForm(Model model) {
        return "staff/wireless-network";
    }

    @GetMapping("/ip-mac")
    public String showIpMacForm(Model model) {
        return "staff/ip-mac";
    }

    @GetMapping("/cloud-account")
    public String showCloudAccountForm(Model model) {
        return "staff/cloud-account";
    }

    @GetMapping("/vpn")
    public String showVpnForm(Model model) {
        return "staff/vpn";
    }

    @GetMapping("/web-academic")
    public String showWebAcademicForm(Model model) {
        return "staff/web-academic";
    }

    @GetMapping("/server-setup")
    public String showServerSetupForm(Model model) {
        return "staff/server-setup";
    }

    @GetMapping("/index")
    public String showIndex(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        
        String identityNumber = principal.getName();
        User user = userService.findByidentityNumber(identityNumber);
        Staff staff = staffService.findByTcKimlikNo(identityNumber);
        
        // Başvuru durumlarını kontrol et
        boolean hasMailApp = false;
        boolean hasEduroamApp = false;
        
        if (user != null) {
            model.addAttribute("user", user);
            // Mail başvurusu kontrolü
            MailFormData mailForm = mailFormRepository.findByUsername(user.getIdentityNumber());
            hasMailApp = (mailForm != null);
            
            // Eduroam başvurusu kontrolü
            EduroamFormData eduroamForm = eduroamFormRepository.findByUsername(user.getIdentityNumber());
            hasEduroamApp = (eduroamForm != null);
            
            // Form DTO'ları için kullanıcı adını set et
            MailFormDto mailFormDto = new MailFormDto();
            EduroamFormDto eduroamFormDto = new EduroamFormDto();
            mailFormDto.setUsername(user.getIdentityNumber());
            eduroamFormDto.setUsername(user.getIdentityNumber());
            model.addAttribute("mailFormDto", mailFormDto);
            model.addAttribute("eduroamFormDto", eduroamFormDto);
        }
        
        if (staff != null) {
            model.addAttribute("staff", staff);
        }
        
        model.addAttribute("userType", "STAFF");
        model.addAttribute("hasMailApplication", hasMailApp);
        model.addAttribute("hasEduroamApplication", hasEduroamApp);
        
        return "staff/index";
    }
} 