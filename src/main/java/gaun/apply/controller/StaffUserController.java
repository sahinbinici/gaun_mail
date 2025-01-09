package gaun.apply.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import gaun.apply.dto.EduroamFormDto;
import gaun.apply.dto.MailFormDto;
import gaun.apply.entity.Staff;
import gaun.apply.entity.user.User;
import gaun.apply.service.StaffService;
import gaun.apply.service.UserService;

@Controller
@RequestMapping("/staff")
public class StaffUserController {
    private final UserService userService;
    private final StaffService staffService;

    public StaffUserController(UserService userService, StaffService staffService) {
        this.userService = userService;
        this.staffService = staffService;
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
        
        MailFormDto mailFormDto = new MailFormDto();
        EduroamFormDto eduroamFormDto = new EduroamFormDto();
        
        if (user != null) {
            model.addAttribute("user", user);
            mailFormDto.setUsername(user.getIdentityNumber());
            eduroamFormDto.setUsername(user.getIdentityNumber());
        }
        if (staff != null) {
            model.addAttribute("staff", staff);
        }
        
        model.addAttribute("userType", "STAFF");
        model.addAttribute("mailFormDto", mailFormDto);
        model.addAttribute("eduroamFormDto", eduroamFormDto);
        
        return "staff/index";
    }
} 