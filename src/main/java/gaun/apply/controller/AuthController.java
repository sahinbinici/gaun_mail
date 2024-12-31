package gaun.apply.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import gaun.apply.dto.MailFormDto;
import gaun.apply.dto.StudentDto;
import gaun.apply.dto.UserDto;
import gaun.apply.entity.User;
import gaun.apply.service.UserService;
import jakarta.validation.Valid;

@Controller
public class AuthController {
    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    // handler method to handle home page request
    @GetMapping("/index")
    public String home(Model model, Principal principal) {
        if (principal != null) {
            User user = userService.findByidentityNumber(principal.getName());
            model.addAttribute("user", user);
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
    public String mailApply(Model model) {
        return "mail-apply";
    }

    @PostMapping("/mail/apply")
    public String mailApply(@Valid @ModelAttribute("mailForm") MailFormDto mailForm, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "index";
        }
        userService.saveMailApply(mailForm);
        return "redirect:/mail/apply?success";
    }


    // handler method to handle user registration form submit request
    @PostMapping("/register/save/student")
    public String registrationStudent(@Valid @ModelAttribute("studentDto") StudentDto studentDto,
                               BindingResult result,
                               Model model) {
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
}