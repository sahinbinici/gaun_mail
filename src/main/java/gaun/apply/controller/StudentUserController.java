package gaun.apply.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import gaun.apply.dto.EduroamFormDto;
import gaun.apply.dto.MailFormDto;
import gaun.apply.dto.StudentDto;
import gaun.apply.entity.form.EduroamFormData;
import gaun.apply.entity.form.MailFormData;
import gaun.apply.entity.user.User;
import gaun.apply.repository.form.EduroamFormRepository;
import gaun.apply.repository.form.MailFormRepository;
import gaun.apply.service.StudentService;
import gaun.apply.service.UserService;

@Controller
@RequestMapping("/student")
public class StudentUserController {
    private final UserService userService;
    private final StudentService studentService;
    private final MailFormRepository mailFormRepository;
    private final EduroamFormRepository eduroamFormRepository;

    public StudentUserController(UserService userService, 
                               StudentService studentService,
                               MailFormRepository mailFormRepository,
                               EduroamFormRepository eduroamFormRepository) {
        this.userService = userService;
        this.studentService = studentService;
        this.mailFormRepository = mailFormRepository;
        this.eduroamFormRepository = eduroamFormRepository;
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
        
        if (student != null) {
            model.addAttribute("student", student);
        }
        
        model.addAttribute("userType", "STUDENT");
        model.addAttribute("hasMailApplication", hasMailApp);
        model.addAttribute("hasEduroamApplication", hasEduroamApp);
        
        return "student/index";
    }
} 