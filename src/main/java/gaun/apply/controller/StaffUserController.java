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
import gaun.apply.dto.WirelessNetworkFormDto;
import gaun.apply.entity.EduroamFormData;
import gaun.apply.entity.MailFormData;
import gaun.apply.entity.Staff;
import gaun.apply.entity.form.CloudAccountFormData;
import gaun.apply.entity.form.IpMacFormData;
import gaun.apply.entity.form.VpnFormData;
import gaun.apply.entity.form.WirelessNetworkFormData;
import gaun.apply.entity.user.User;
import gaun.apply.repository.EduroamFormRepository;
import gaun.apply.repository.MailFormRepository;
import gaun.apply.repository.form.CloudAccountFormRepository;
import gaun.apply.repository.form.IpMacFormRepository;
import gaun.apply.repository.form.VpnFormRepository;
import gaun.apply.repository.form.WirelessNetworkFormRepository;
import gaun.apply.service.StaffService;
import gaun.apply.service.UserService;
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
    private WirelessNetworkFormRepository wirelessNetworkFormRepository;
    @Autowired
    private IpMacFormRepository ipMacFormRepository;

    public StaffUserController(UserService userService, 
                             StaffService staffService,
                             MailFormRepository mailFormRepository,
                             EduroamFormRepository eduroamFormRepository) {
        this.userService = userService;
        this.staffService = staffService;
        this.mailFormRepository = mailFormRepository;
        this.eduroamFormRepository = eduroamFormRepository;
    }

    @GetMapping("/wireless-network")
    public String showWirelessNetworkForm(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        
        WirelessNetworkFormDto formDto = new WirelessNetworkFormDto();
        formDto.setTcKimlikNo(principal.getName());
        model.addAttribute("wirelessNetworkFormDto", formDto);
        
        return "fragments/wireless-network :: content";
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
            return "redirect:/staff/index?cloudSuccess";
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
            return "redirect:/staff/index?vpnSuccess";
        } catch (Exception e) {
            model.addAttribute("error", "Başvuru sırasında bir hata oluştu");
            return "fragments/vpn :: content";
        }
    }

    @GetMapping("/web-academic")
    public String showWebAcademicForm(Model model) {
        return "staff/web-academic";
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
            MailFormData mailForm = mailFormRepository.findByUsername(user.getIdentityNumber());
            EduroamFormData eduroamForm = eduroamFormRepository.findByUsername(user.getIdentityNumber());
            WirelessNetworkFormData wirelessForm = wirelessNetworkFormRepository.findByTcKimlikNo(identityNumber);
            IpMacFormData ipMacForm = ipMacFormRepository.findByTcKimlikNo(identityNumber);
            CloudAccountFormData cloudForm = cloudAccountFormRepository.findByTcKimlikNo(identityNumber);
            VpnFormData vpnForm = vpnFormRepository.findByTcKimlikNo(identityNumber);
            
            // Başvuru durumlarını model'e ekle
            model.addAttribute("hasMailApplication", mailForm != null);
            model.addAttribute("hasEduroamApplication", eduroamForm != null);
            model.addAttribute("hasWirelessApplication", wirelessForm != null);
            model.addAttribute("hasIpMacApplication", ipMacForm != null);
            model.addAttribute("hasCloudApplication", cloudForm != null);
            model.addAttribute("hasVpnApplication", vpnForm != null);
            
            // Form verilerini model'e ekle
            model.addAttribute("mailForm", mailForm);
            model.addAttribute("eduroamForm", eduroamForm);
            model.addAttribute("wirelessForm", wirelessForm);
            model.addAttribute("ipMacForm", ipMacForm);
            model.addAttribute("cloudForm", cloudForm);
            model.addAttribute("vpnForm", vpnForm);
            
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
        WirelessNetworkFormDto wirelessFormDto = new WirelessNetworkFormDto();
        IpMacFormDto ipMacFormDto = new IpMacFormDto();
        CloudAccountFormDto cloudFormDto = new CloudAccountFormDto();
        VpnFormDto vpnFormDto = new VpnFormDto();
        
        // TC Kimlik No'yu set et
        mailFormDto.setUsername(tcKimlikNo);
        eduroamFormDto.setUsername(tcKimlikNo);
        wirelessFormDto.setTcKimlikNo(tcKimlikNo);
        ipMacFormDto.setTcKimlikNo(tcKimlikNo);
        cloudFormDto.setTcKimlikNo(tcKimlikNo);
        vpnFormDto.setTcKimlikNo(tcKimlikNo);
        
        // Model'e ekle
        model.addAttribute("mailFormDto", mailFormDto);
        model.addAttribute("eduroamFormDto", eduroamFormDto);
        model.addAttribute("wirelessNetworkFormDto", wirelessFormDto);
        model.addAttribute("ipMacFormDto", ipMacFormDto);
        model.addAttribute("cloudAccountFormDto", cloudFormDto);
        model.addAttribute("vpnFormDto", vpnFormDto);
    }

    @PostMapping("/wireless-network/apply")
    public String applyWirelessNetwork(@Valid @ModelAttribute("wirelessNetworkFormDto") WirelessNetworkFormDto formDto,
                                     BindingResult result,
                                     Model model) {
        if (result.hasErrors()) {
            return "fragments/wireless-network :: content";
        }

        try {
            WirelessNetworkFormData formData = new WirelessNetworkFormData();
            formData.setTcKimlikNo(formDto.getTcKimlikNo());
            formData.setMacAddress(formDto.getMacAddress());
            formData.setDeviceType(formDto.getDeviceType());
            formData.setApplyDate(LocalDateTime.now());
            formData.setStatus(false);
            
            wirelessNetworkFormRepository.save(formData);
            return "redirect:/staff/index?wirelessSuccess";
        } catch (Exception e) {
            model.addAttribute("error", "Başvuru sırasında bir hata oluştu");
            return "fragments/wireless-network :: content";
        }
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
            return "redirect:/staff/index?ipMacSuccess";
        } catch (Exception e) {
            model.addAttribute("error", "Başvuru sırasında bir hata oluştu");
            return "fragments/ip-mac :: content";
        }
    }
} 