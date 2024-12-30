package gaun.apply.controller;

import gaun.apply.dto.StudentDto;
import jakarta.validation.Valid;
import gaun.apply.dto.UserDto;
import gaun.apply.entity.User;
import gaun.apply.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class AuthController {
    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    // handler method to handle home page request
    @GetMapping("/index")
    public String home() {
        return "index";
    }

    // handler method to handle user registration form request
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        // create model object to store form data
        StudentDto studentDto = new StudentDto();
        model.addAttribute("studentDto", studentDto);
        return "register";
    }

    // handler method to handle user registration form submit request
    @PostMapping("/register/save")
    public String registration(@Valid @ModelAttribute("user") StudentDto studentDto,
                               BindingResult result,
                               Model model) {
        User existingUser = userService.findByidentityNumber(studentDto.getOgrenciNo());

        if (existingUser != null && existingUser.getIdentityNumber() != null && !existingUser.getIdentityNumber().isEmpty()) {
            result.rejectValue("email", null,
                    "There is already an account registered with the same email");
        }

        if (result.hasErrors()) {
            model.addAttribute("user", studentDto);
            return "/register";
        }

        userService.saveUser(studentDto);
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