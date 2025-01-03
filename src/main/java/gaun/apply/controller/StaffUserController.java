package gaun.apply.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import gaun.apply.dto.UserDto;
import gaun.apply.entity.user.User;
import gaun.apply.service.UserService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/staff")
public class StaffUserController {
    private final UserService userService;

    public StaffUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        UserDto userDto = new UserDto();
        model.addAttribute("userDto", userDto);
        return "register";
    }

    @PostMapping("/register/save")
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
} 