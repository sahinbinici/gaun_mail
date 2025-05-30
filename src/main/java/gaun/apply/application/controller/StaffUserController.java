package gaun.apply.application.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import gaun.apply.application.dto.EduroamFormDto;
import gaun.apply.application.dto.MailFormDto;
import gaun.apply.domain.user.entity.Staff;
import gaun.apply.domain.eduroam.entity.EduroamFormData;
import gaun.apply.domain.mail.entity.MailFormData;
import gaun.apply.domain.user.entity.User;
import gaun.apply.domain.user.service.StaffService;
import gaun.apply.domain.user.service.UserService;
import gaun.apply.domain.eduroam.service.EduroamFormService;
import gaun.apply.domain.mail.service.MailFormService;

@Controller
@RequestMapping("/staff")
public class StaffUserController {
    private final UserService userService;
    private final StaffService staffService;
    private final MailFormService mailFormService;
    private final EduroamFormService eduroamFormService;

    public StaffUserController(UserService userService, 
                               StaffService staffService, 
                               MailFormService mailFormService, 
                               EduroamFormService eduroamFormService) {
        this.userService = userService;
        this.staffService = staffService;
        this.mailFormService = mailFormService;
        this.eduroamFormService = eduroamFormService;
    }

    @GetMapping("/index")
    public String showIndex(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        
        String identityNumber = principal.getName();
        User user = userService.findByidentityNumber(identityNumber);
        Staff staff = staffService.findByTcKimlikNo(identityNumber);
        
        if (user != null) {
            model.addAttribute("user", user);
            
            // Başvuru durumlarını kontrol et
            // Personel için TC Kimlik No ile kontrol ediyoruz (memory içinde belirtildiği gibi)
            MailFormData mailForm = mailFormService.findByTcKimlikNo(user.getTcKimlikNo());
            EduroamFormData eduroamForm = eduroamFormService.eduroamFormData(user.getIdentityNumber());
            
            // Başvuru durumlarını model'e ekle
            model.addAttribute("hasMailApplication", mailForm != null);
            model.addAttribute("hasEduroamApplication", eduroamForm != null);
            
            // Form verilerini model'e ekle
            model.addAttribute("mailForm", mailForm);
            model.addAttribute("eduroamForm", eduroamForm);
            // Form DTO'larını ekle
            addFormDtosToModel(model, identityNumber);
        }
        
        if (staff != null) {
            model.addAttribute("staff", staff);
        }
        
        model.addAttribute("userType", "STAFF");
        return "staff/index";
    }

    private void addFormDtosToModel(Model model, String tcKimlikNo) {
        MailFormDto mailFormDto = new MailFormDto();
        EduroamFormDto eduroamFormDto = new EduroamFormDto();
        
        // TC Kimlik No'yu set et
        mailFormDto.setUsername(tcKimlikNo);
        mailFormDto.setTcKimlikNo(tcKimlikNo);  // Set TC Kimlik No for mail form
        eduroamFormDto.setUsername(tcKimlikNo);
        eduroamFormDto.setTcKimlikNo(tcKimlikNo);

        // Model'e ekle
        model.addAttribute("mailFormDto", mailFormDto);
        model.addAttribute("eduroamFormDto", eduroamFormDto);
    }
}
