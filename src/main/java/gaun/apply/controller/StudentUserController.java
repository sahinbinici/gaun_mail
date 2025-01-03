package gaun.apply.controller;

import java.security.NoSuchAlgorithmException;
import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import gaun.apply.dto.StudentDto;
import gaun.apply.entity.user.User;
import gaun.apply.repository.MailFormRepository;
import gaun.apply.service.StudentService;
import gaun.apply.service.UserService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/student")
public class StudentUserController {
    private final UserService userService;
    private final StudentService studentService;
    private final MailFormRepository mailFormRepository;

    public StudentUserController(UserService userService, StudentService studentService, MailFormRepository mailFormRepository) {
        this.userService = userService;
        this.studentService = studentService;
        this.mailFormRepository = mailFormRepository;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        StudentDto studentDto = new StudentDto();
        model.addAttribute("studentDto", studentDto);
        return "register";
    }

    @PostMapping("/register/save")
    public String registrationStudent(@Valid @ModelAttribute("studentDto") StudentDto studentDto,
                               BindingResult result,
                               Model model) throws NoSuchAlgorithmException {
        User existingUser = userService.findByidentityNumber(studentDto.getOgrenciNo());

        if (existingUser != null) {
            result.rejectValue("ogrenciNo", null, "Bu öğrenci numarası ile daha önce kayıt yapılmış");
        }

        if (result.hasErrors()) {
            model.addAttribute("user", studentDto);
            return "/register";
        }
        userService.saveUserStudent(studentDto);
        studentService.saveStudent(studentDto);
        return "redirect:/register?success";
    }

    @GetMapping("/bilgilerim")
    public String bilgilerim(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        
        StudentDto student = studentService.findByOgrenciNo(principal.getName());
        if (student != null) {
            model.addAttribute("student", student);
        }
        
        return "bilgilerim";
    }
} 