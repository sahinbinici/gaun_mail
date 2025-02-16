package gaun.apply.controller;

import java.security.Principal;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import gaun.apply.dto.CloudAccountFormDto;
import gaun.apply.dto.EduroamFormDto;
import gaun.apply.dto.IpMacFormDto;
import gaun.apply.dto.MailFormDto;
import gaun.apply.dto.VpnFormDto;
import gaun.apply.dto.WebAcademicFormDto;
import gaun.apply.entity.Staff;
import gaun.apply.entity.form.CloudAccountFormData;
import gaun.apply.entity.form.EduroamFormData;
import gaun.apply.entity.form.IpMacFormData;
import gaun.apply.entity.form.MailFormData;
import gaun.apply.entity.form.VpnFormData;
import gaun.apply.entity.form.WebAcademicFormData;
import gaun.apply.entity.user.User;
import gaun.apply.repository.form.CloudAccountFormRepository;
import gaun.apply.repository.form.EduroamFormRepository;
import gaun.apply.repository.form.IpMacFormRepository;
import gaun.apply.repository.form.MailFormRepository;
import gaun.apply.repository.form.VpnFormRepository;
import gaun.apply.service.StaffService;
import gaun.apply.service.UserService;
import gaun.apply.service.form.EduroamFormService;
import gaun.apply.service.form.MailFormService;
import gaun.apply.service.form.VpnFormService;
import gaun.apply.service.form.WebAcademicFormService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/staff")
public class StaffUserController {
    private final UserService userService;
    private final StaffService staffService;
    private final MailFormRepository mailFormRepository;
    private final EduroamFormRepository eduroamFormRepository;
    @Autowired
    private CloudAccountFormRepository cloudAccountFormRepository;
    @Autowired
    private VpnFormRepository vpnFormRepository;
    @Autowired
    private IpMacFormRepository ipMacFormRepository;
    @Autowired
    private MailFormService mailFormService;
    @Autowired
    private EduroamFormService eduroamFormService;
    @Autowired
    private VpnFormService vpnFormService;
    @Autowired
    private WebAcademicFormService webAcademicFormService;

    public StaffUserController(UserService userService, 
                             StaffService staffService,
                             MailFormRepository mailFormRepository,
                             EduroamFormRepository eduroamFormRepository) {
        this.userService = userService;
        this.staffService = staffService;
        this.mailFormRepository = mailFormRepository;
        this.eduroamFormRepository = eduroamFormRepository;
    }

    @GetMapping("/ip-mac")
    public String showIpMacForm(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        
        IpMacFormDto formDto = new IpMacFormDto();
        formDto.setTcKimlikNo(principal.getName());
        model.addAttribute("ipMacFormDto", formDto);
        
        return "fragments/ip-mac :: content";
    }

    @GetMapping("/cloud-account")
    public String showCloudAccountForm(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        
        CloudAccountFormDto formDto = new CloudAccountFormDto();
        formDto.setTcKimlikNo(principal.getName());
        model.addAttribute("cloudAccountFormDto", formDto);
        
        return "fragments/cloud-account :: content";
    }

    @PostMapping("/cloud-account/apply")
    public String applyCloudAccount(@Valid @ModelAttribute("cloudAccountFormDto") CloudAccountFormDto formDto,
                                  BindingResult result,
                                  Model model) {
        if (result.hasErrors()) {
            return "fragments/cloud-account :: content";
        }

        try {
            CloudAccountFormData formData = new CloudAccountFormData();
            formData.setTcKimlikNo(formDto.getTcKimlikNo());
            formData.setPurpose(formDto.getPurpose());
            formData.setCapacity(formDto.getCapacity());
            formData.setApplyDate(LocalDateTime.now());
            formData.setStatus(false);
            
            cloudAccountFormRepository.save(formData);
            return "redirect:/staff/cloud-account/success";
        } catch (Exception e) {
            model.addAttribute("error", "Başvuru sırasında bir hata oluştu");
            return "fragments/cloud-account :: content";
        }
    }

    @GetMapping("/vpn")
    public String showVpnForm(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        
        VpnFormDto formDto = new VpnFormDto();
        formDto.setTcKimlikNo(principal.getName());
        model.addAttribute("vpnFormDto", formDto);
        
        return "fragments/vpn :: content";
    }

    @PostMapping("/vpn/apply")
    public String applyVpn(@Valid @ModelAttribute("vpnFormDto") VpnFormDto formDto,
                          BindingResult result,
                          Model model) {
        if (result.hasErrors()) {
            return "fragments/vpn :: content";
        }

        try {
            VpnFormData formData = new VpnFormData();
            formData.setTcKimlikNo(formDto.getTcKimlikNo());
            formData.setPurpose(formDto.getPurpose());
            formData.setIpAddress(formDto.getIpAddress());
            formData.setApplyDate(LocalDateTime.now());
            formData.setStatus(false);
            
            vpnFormRepository.save(formData);
            return "redirect:/staff/vpn/success";
        } catch (Exception e) {
            model.addAttribute("error", "Başvuru sırasında bir hata oluştu");
            return "fragments/vpn :: content";
        }
    }

    @GetMapping("/web-academic")
    public String showWebAcademicForm(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        
        WebAcademicFormDto formDto = new WebAcademicFormDto();
        formDto.setTcKimlikNo(principal.getName());
        model.addAttribute("webAcademicFormDto", formDto);
        
        // Mevcut başvuruyu kontrol et
        WebAcademicFormData existingForm = webAcademicFormService.findByTcKimlikNo(principal.getName());
        if (existingForm != null) {
            model.addAttribute("webAcademicForm", existingForm);
        }
        
        return "fragments/web-academic :: content";
    }

    @PostMapping("/web-academic/apply")
    public String applyWebAcademic(@Valid @ModelAttribute("webAcademicFormDto") WebAcademicFormDto formDto,
                                  BindingResult result,
                                  Model model) {
        if (result.hasErrors()) {
            return "fragments/web-academic :: content";
        }

        try {
            WebAcademicFormData formData = new WebAcademicFormData();
            formData.setTcKimlikNo(formDto.getTcKimlikNo());
            formData.setDomainName(formDto.getDomainName());
            formData.setFtpUsername(formDto.getFtpUsername());
            formData.setMysqlUsername(formDto.getMysqlUsername());
            formData.setPurpose(formDto.getPurpose());
            formData.setHostingType(formDto.getHostingType());
            formData.setApplyDate(LocalDateTime.now());
            formData.setStatus(false);
            
            webAcademicFormService.saveWebAcademicFormData(formData);
            return "redirect:/staff/web-academic/success";
        } catch (Exception e) {
            model.addAttribute("error", "Başvuru sırasında bir hata oluştu");
            return "fragments/web-academic :: content";
        }
    }

    @GetMapping("/server-setup")
    public String showServerSetupForm(Model model) {
        return "staff/server-setup";
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
            
            // Tüm başvuru durumlarını kontrol et
            MailFormData mailForm = mailFormService.mailFormData(user.getIdentityNumber());
            EduroamFormData eduroamForm = eduroamFormService.eduroamFormData(user.getIdentityNumber());
            IpMacFormData ipMacForm = ipMacFormRepository.findByTcKimlikNo(identityNumber);
            CloudAccountFormData cloudForm = cloudAccountFormRepository.findByTcKimlikNo(identityNumber);
            VpnFormData vpnForm = vpnFormService.findByTcKimlikNo(identityNumber);
            WebAcademicFormData webAcademicForm = webAcademicFormService.findByTcKimlikNo(identityNumber);
            
            // Başvuru durumlarını model'e ekle
            model.addAttribute("hasMailApplication", mailForm != null);
            model.addAttribute("hasEduroamApplication", eduroamForm != null);
            model.addAttribute("hasIpMacApplication", ipMacForm != null);
            model.addAttribute("hasCloudApplication", cloudForm != null);
            model.addAttribute("hasVpnApplication", vpnForm != null);
            model.addAttribute("hasWebAcademicApplication", webAcademicForm != null);
            
            // Form verilerini model'e ekle
            model.addAttribute("mailForm", mailForm);
            model.addAttribute("eduroamForm", eduroamForm);
            model.addAttribute("ipMacForm", ipMacForm);
            model.addAttribute("cloudForm", cloudForm);
            model.addAttribute("vpnForm", vpnForm);
            model.addAttribute("webAcademicForm", webAcademicForm);
            
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
        IpMacFormDto ipMacFormDto = new IpMacFormDto();
        CloudAccountFormDto cloudFormDto = new CloudAccountFormDto();
        VpnFormDto vpnFormDto = new VpnFormDto();
        WebAcademicFormDto webAcademicFormDto = new WebAcademicFormDto();
        
        // TC Kimlik No'yu set et
        mailFormDto.setUsername(tcKimlikNo);
        eduroamFormDto.setUsername(tcKimlikNo);
        ipMacFormDto.setTcKimlikNo(tcKimlikNo);
        cloudFormDto.setTcKimlikNo(tcKimlikNo);
        vpnFormDto.setTcKimlikNo(tcKimlikNo);
        webAcademicFormDto.setTcKimlikNo(tcKimlikNo);
        
        // Model'e ekle
        model.addAttribute("mailFormDto", mailFormDto);
        model.addAttribute("eduroamFormDto", eduroamFormDto);
        model.addAttribute("ipMacFormDto", ipMacFormDto);
        model.addAttribute("cloudAccountFormDto", cloudFormDto);
        model.addAttribute("vpnFormDto", vpnFormDto);
        model.addAttribute("webAcademicFormDto", webAcademicFormDto);
    }

    @PostMapping("/ip-mac/apply")
    public String applyIpMac(@Valid @ModelAttribute("ipMacFormDto") IpMacFormDto formDto,
                           BindingResult result,
                           Model model) {
        if (result.hasErrors()) {
            return "fragments/ip-mac :: content";
        }

        try {
            IpMacFormData formData = new IpMacFormData();
            formData.setTcKimlikNo(formDto.getTcKimlikNo());
            formData.setMacAddress(formDto.getMacAddress());
            formData.setIpAddress(formDto.getIpAddress());
            formData.setLocation(formDto.getLocation());
            formData.setApplyDate(LocalDateTime.now());
            formData.setStatus(false);
            
            ipMacFormRepository.save(formData);
            return "redirect:/staff/ip-mac/success";
        } catch (Exception e) {
            model.addAttribute("error", "Başvuru sırasında bir hata oluştu");
            return "fragments/ip-mac :: content";
        }
    }

    // Success page mappings
    @GetMapping("/ip-mac/success")
    public String showIpMacSuccess() {
        return "ip-mac/apply-success";
    }

    @GetMapping("/cloud-account/success")
    public String showCloudAccountSuccess() {
        return "cloud-account/apply-success";
    }

    @GetMapping("/vpn/success")
    public String showVpnSuccess() {
        return "vpn/apply-success";
    }

    @GetMapping("/web-academic/success")
    public String showWebAcademicSuccess() {
        return "web-academic/apply-success";
    }
} 