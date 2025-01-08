package gaun.apply.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import gaun.apply.dto.EduroamFormDto;
import gaun.apply.dto.MailFormDto;
import gaun.apply.dto.StudentDto;
import gaun.apply.entity.user.User;
import gaun.apply.service.StudentService;
import gaun.apply.service.UserService;

@Controller
@RequestMapping("/student")
public class StudentUserController {
    private final UserService userService;
    private final StudentService studentService;

    public StudentUserController(UserService userService, StudentService studentService) {
        this.userService = userService;
        this.studentService = studentService;
    }

    @GetMapping("/index")
    public String showIndex(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        
        String identityNumber = principal.getName();
        User user = userService.findByidentityNumber(identityNumber);
        StudentDto student = studentService.findByOgrenciNo(identityNumber);
        
        MailFormDto mailFormDto = new MailFormDto();
        EduroamFormDto eduroamFormDto = new EduroamFormDto();
        
        if (user != null) {
            model.addAttribute("user", user);
            mailFormDto.setUsername(user.getIdentityNumber());
            eduroamFormDto.setUsername(user.getIdentityNumber());
        }
        if (student != null) {
            model.addAttribute("student", student);
        }
        
        model.addAttribute("userType", "STUDENT");
        model.addAttribute("mailFormDto", mailFormDto);
        model.addAttribute("eduroamFormDto", eduroamFormDto);
        
        return "student/index";
    }
} 