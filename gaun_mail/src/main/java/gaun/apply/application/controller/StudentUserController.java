package gaun.apply.application.controller;

import java.security.Principal;

import gaun.apply.domain.eduroam.service.EduroamFormService;
import gaun.apply.domain.mail.service.MailFormService;
import gaun.apply.domain.user.service.StudentService;
import gaun.apply.domain.user.service.UserService;
import gaun.apply.application.dto.StudentDto;
import gaun.apply.application.dto.EduroamFormDto;
import gaun.apply.application.dto.MailFormDto;
import gaun.apply.domain.user.entity.User;
import gaun.apply.domain.eduroam.entity.EduroamFormData;
import gaun.apply.domain.mail.entity.MailFormData;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/student")
public class StudentUserController {
    private final UserService userService;
    private final StudentService studentService;
    private final MailFormService mailFormService;
    private final EduroamFormService eduroamFormService;

    public StudentUserController(UserService userService,
                                 StudentService studentService, 
                                 MailFormService mailFormService, 
                                 EduroamFormService eduroamFormService) {
        this.userService = userService;
        this.studentService = studentService;
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
        StudentDto student = studentService.findByOgrenciNo(identityNumber);
        
        // Başvuru durumlarını kontrol et
        boolean hasMailApp = false;
        boolean hasEduroamApp = false;
        
        if (user != null) {
            model.addAttribute("user", user);
            // Mail başvurusu kontrolü
            MailFormData mailForm = mailFormService.findByTcKimlikNo(user.getTcKimlikNo());
            hasMailApp = (mailForm != null);
            
            // Eduroam başvurusu kontrolü
            EduroamFormData eduroamForm = eduroamFormService.eduroamFormData(user.getTcKimlikNo());
            hasEduroamApp = (eduroamForm != null);
            
            // Form DTO'ları için kullanıcı adını set et
            MailFormDto mailFormDto = new MailFormDto();
            EduroamFormDto eduroamFormDto = new EduroamFormDto();
            mailFormDto.setOgrenciNo(user.getIdentityNumber());
            mailFormDto.setTcKimlikNo(user.getTcKimlikNo());
            mailFormDto.setEmail(studentService.createEmailAddress(identityNumber));
            eduroamFormDto.setTcKimlikNo(user.getTcKimlikNo());
            eduroamFormDto.setOgrenciNo(user.getIdentityNumber()!=null ? user.getIdentityNumber() : "");
            model.addAttribute("mailFormDto", mailFormDto);
            model.addAttribute("eduroamFormDto", eduroamFormDto);
        }
        
        if (student != null) {
            model.addAttribute("student", student);
        }
        
        model.addAttribute("userType", "STUDENT");
        model.addAttribute("hasMailApplication", hasMailApp);
        model.addAttribute("hasEduroamApplication", hasEduroamApp);
        
        return "student/index";
    }
}
