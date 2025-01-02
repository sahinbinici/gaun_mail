package gaun.apply.controller;

import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import gaun.apply.dto.MailFormDto;
import gaun.apply.dto.StudentDto;
import gaun.apply.dto.UserDto;
import gaun.apply.entity.MailFormData;
import gaun.apply.entity.User;
import gaun.apply.repository.MailFormRepository;
import gaun.apply.service.StudentService;
import gaun.apply.service.UserService;
import jakarta.validation.Valid;

@Controller
public class AuthController {
    private final UserService userService;
    private final StudentService studentService;
    private final MailFormRepository mailFormRepository;

    public AuthController(UserService userService, StudentService studentService, MailFormRepository mailFormRepository) {
        this.userService = userService;
        this.studentService = studentService;
        this.mailFormRepository = mailFormRepository;
    }

    // handler method to handle home page request
    @GetMapping("/index")
    public String home(Model model, Principal principal) {
        if (principal != null) {
            User user = userService.findByidentityNumber(principal.getName());
            if (user != null) {
                model.addAttribute("user", user);
                
                // Mail başvurusu kontrolü
                MailFormData existingMailForm = mailFormRepository.findByUsername(user.getIdentityNumber());
                model.addAttribute("existingMailForm", existingMailForm);
            } else {
                return "redirect:/login";
            }
        } else {
            return "redirect:/login";
        }
        return "index";
    }

    // handler method to handle user registration form request
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        // create model object to store form data
        StudentDto studentDto = new StudentDto();
        UserDto userDto = new UserDto();
        model.addAttribute("studentDto", studentDto);
        model.addAttribute("userDto", userDto);
        return "register";
    }

    @GetMapping("/mail/apply")
    public String showMailApplyResult(@RequestParam(required = false) String mailExists, Model model) {
        if (mailExists != null && mailExists.equals("true")) {
            model.addAttribute("error", "Bu kullanıcı için daha önce mail başvurusu yapılmış.");
            return "redirect:/index";
        }
        return "mail-apply";
    }

    @PostMapping("/mail/apply")
    public String mailApply(@Valid @ModelAttribute("mailForm") MailFormDto mailForm, 
                           BindingResult result, 
                           Model model) {
        // Mevcut başvuru kontrolü
        MailFormData existingMailForm = mailFormRepository.findByUsername(mailForm.getUsername());
        
        if (existingMailForm != null) {
            // Kullanıcıya özel hata mesajı
            model.addAttribute("mailError", "Bu kullanıcı için daha önce mail başvurusu yapılmış. " +
                    "Mail adresi: " + existingMailForm.getEmail() + "@gantep.edu.tr");
            return "redirect:/index?mailExists=true";
        }

        if (result.hasErrors()) {
            return "index";
        }

        // Yeni başvuruyu kaydet
        userService.saveMailApply(mailForm);
        return "redirect:/mail/apply?success";
    }

    // handler method to handle user registration form submit request
    @PostMapping("/register/save/student")
    public String registrationStudent(@Valid @ModelAttribute("studentDto") StudentDto studentDto,
                               BindingResult result,
                               Model model) throws NoSuchAlgorithmException {
        User existingUser = userService.findByidentityNumber(studentDto.getOgrenciNo());

        if (existingUser != null) {
            result.rejectValue("ogrenciNo", null,
                    "Bu öğrenci numarası ile daha önce kayıt yapılmış");
        }

        if (result.hasErrors()) {
            model.addAttribute("user", studentDto);
            return "/register";
        }
        userService.saveUserStudent(studentDto);
        studentService.saveStudent(studentDto);
        return "redirect:/register?success";
    }

    @PostMapping("/register/save/personnel")
    public String registrationStaff(@Valid @ModelAttribute("userDto") UserDto userDto,
                               BindingResult result,
                               Model model) {
        User existingUser = userService.findByidentityNumber(userDto.getTcKimlikNo());

        if (existingUser != null && existingUser.getIdentityNumber() != null && !existingUser.getIdentityNumber().isEmpty()) {
            result.rejectValue("email", null,
                    "There is already an account registered with the same email");
        }

        if (result.hasErrors()) {
            model.addAttribute("user", userDto);
            return "/register";
        }

        userService.saveUserStaff(userDto);
        return "redirect:/register?success";
    }

    // handler method to handle list of users
    @GetMapping("/users")
    public String users(Model model) {
        List<UserDto> users = userService.findAllUsers();
        model.addAttribute("users", users);
        return "users";
    }


    @GetMapping("/login")
    public String login(@ModelAttribute("user") User user) {
        return "login";
    }

    @GetMapping("/check-mail-exists/{username}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkMailExists(@PathVariable String username) {
        MailFormData existingMail = mailFormRepository.findByUsername(username);
        Map<String, Object> response = new HashMap<>();
        
        if (existingMail != null) {
            response.put("exists", true);
            response.put("email", existingMail.getEmail() + "@gantep.edu.tr");
            return ResponseEntity.ok(response);
        }
        
        response.put("exists", false);
        return ResponseEntity.ok(response);
    }
}