package gaun.apply.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import gaun.apply.dto.MailFormDto;
import gaun.apply.entity.MailFormData;
import gaun.apply.entity.user.User;
import gaun.apply.repository.MailFormRepository;
import gaun.apply.service.StudentService;
import gaun.apply.service.UserService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/")
public class BaseController {
    private final UserService userService;
    private final StudentService studentService;
    private final MailFormRepository mailFormRepository;

    public BaseController(UserService userService, StudentService studentService, MailFormRepository mailFormRepository) {
        this.userService = userService;
        this.studentService = studentService;
        this.mailFormRepository = mailFormRepository;
    }

    @GetMapping("/index")
    public String home(Model model, Principal principal) {
        try {
            if (principal == null || principal.getName() == null) {
                return "redirect:/login";
            }

            User user = userService.findByidentityNumber(principal.getName());
            if (user == null || user.getIdentityNumber() == null) {
                return "redirect:/login";
            }

            model.addAttribute("user", user);
            model.addAttribute("identityNumber", user.getIdentityNumber());
            model.addAttribute("userName", user.getName());
            model.addAttribute("userLastname", user.getLastname());

            MailFormData existingMailForm = mailFormRepository.findByUsername(user.getIdentityNumber());
            model.addAttribute("existingMailForm", existingMailForm);

            return "index";
        } catch (Exception e) {
            return "redirect:/login";
        }
    }

    @GetMapping("/login")
    public String login(@ModelAttribute("user") User user) {
        return "login";
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
        MailFormData existingMailForm = mailFormRepository.findByUsername(mailForm.getUsername());
        
        if (existingMailForm != null) {
            model.addAttribute("mailError", "Bu kullanıcı için daha önce mail başvurusu yapılmış. " +
                    "Mail adresi: " + existingMailForm.getEmail() + "@gantep.edu.tr");
            return "redirect:/index?mailExists=true";
        }

        if (result.hasErrors()) {
            return "index";
        }

        userService.saveMailApply(mailForm);
        return "redirect:/mail/apply?success";
    }
} 