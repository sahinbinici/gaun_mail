package gaun.apply.service.form;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import gaun.apply.dto.StudentDto;
import gaun.apply.enums.ApplicationStatusEnum;
import gaun.apply.entity.form.BaseFormData;
import gaun.apply.entity.form.CloudAccountFormData;
import gaun.apply.entity.form.EduroamFormData;
import gaun.apply.entity.form.IpMacFormData;
import gaun.apply.entity.form.MailFormData;
import gaun.apply.entity.form.VpnFormData;
import gaun.apply.repository.form.BaseFormRepository;
import gaun.apply.repository.form.CloudAccountFormRepository;
import gaun.apply.repository.form.EduroamFormRepository;
import gaun.apply.repository.form.IpMacFormRepository;
import gaun.apply.repository.form.MailFormRepository;
import gaun.apply.repository.form.VpnFormRepository;
import gaun.apply.service.SmsService;
import gaun.apply.service.StudentService;

@Service
public class FormService {
    private final Map<String, BaseFormRepository<? extends BaseFormData>> repositories;
    private final SmsService smsService;
    private final StudentService studentService;
    private final MailFormRepository mailFormRepository;
    private final EduroamFormRepository eduroamFormRepository;
    private final VpnFormRepository vpnFormRepository;
    private final CloudAccountFormRepository cloudAccountFormRepository;
    private final IpMacFormRepository ipMacFormRepository;
    
    public FormService(List<BaseFormRepository<? extends BaseFormData>> repos,
                       SmsService smsService, 
                       StudentService studentService,
                       MailFormRepository mailFormRepository,
                       EduroamFormRepository eduroamFormRepository,
                       VpnFormRepository vpnFormRepository,
                       CloudAccountFormRepository cloudAccountFormRepository,
                       IpMacFormRepository ipMacFormRepository) {
        repositories = repos.stream()
            .collect(Collectors.toMap(
                r -> r.getClass().getInterfaces()[0].getSimpleName(),
                r -> r
            ));
        this.smsService = smsService;
        this.studentService = studentService;
        this.mailFormRepository = mailFormRepository;
        this.eduroamFormRepository = eduroamFormRepository;
        this.vpnFormRepository = vpnFormRepository;
        this.cloudAccountFormRepository = cloudAccountFormRepository;
        this.ipMacFormRepository = ipMacFormRepository;
    }
    
    public void activateForm(Long id, Class<? extends BaseFormData> formClass) {
        String repoName = formClass.getSimpleName().replace("Data", "Repository");
        @SuppressWarnings("unchecked")
        BaseFormRepository<BaseFormData> repository = 
            (BaseFormRepository<BaseFormData>) repositories.get(repoName);
        
        if (repository == null) {
            throw new RuntimeException("Repository bulunamadı: " + repoName);
        }
        
        BaseFormData form = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Form bulunamadı"));
        form.setApplicationStatus(ApplicationStatusEnum.APPROVED);
        form.setApprovalDate(LocalDateTime.now());
        repository.save(form);
    }

    public void rejectForm(Long id, Class<? extends BaseFormData> formClass, String reason) {
        BaseFormData form = null;

        if (formClass.equals(MailFormData.class)) {
            form = mailFormRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Mail başvurusu bulunamadı"));
            form.setRejected(true);
            form.setRejectionReason(reason);
            form.setRejectionDate(LocalDateTime.now());
            form.setApplicationStatus(ApplicationStatusEnum.REJECTED);
            mailFormRepository.save((MailFormData) form);
            StudentDto student = studentService.findByOgrenciNo(((MailFormData) form).getUsername());
            smsService.sendSms(new String[]{student.getGsm1()}, "GAÜN E-posta başvurunuz reddedildi. Red Sebebi: "+form.getRejectionReason());
        } else if (formClass.equals(EduroamFormData.class)) {
            form = eduroamFormRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Eduroam başvurusu bulunamadı"));
            form.setRejected(true);
            form.setRejectionReason(reason);
            form.setRejectionDate(LocalDateTime.now());
            form.setApplicationStatus(ApplicationStatusEnum.REJECTED);
            eduroamFormRepository.save((EduroamFormData) form);
            StudentDto student = studentService.findByOgrenciNo(((EduroamFormData) form).getUsername());
            smsService.sendSms(new String[]{student.getGsm1()}, "GAÜN Eduroam başvurunuz reddedildi. Red Sebebi: "+form.getRejectionReason());
        } else if (formClass.equals(IpMacFormData.class)) {
            form = ipMacFormRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("IP-MAC başvurusu bulunamadı"));
            form.setRejected(true);
            form.setRejectionReason(reason);
            form.setRejectionDate(LocalDateTime.now());
            form.setApplicationStatus(ApplicationStatusEnum.REJECTED);
            ipMacFormRepository.save((IpMacFormData) form);
        } else if (formClass.equals(CloudAccountFormData.class)) {
            form = cloudAccountFormRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Cloud başvurusu bulunamadı"));
            form.setRejected(true);
            form.setRejectionReason(reason);
            form.setRejectionDate(LocalDateTime.now());
            form.setApplicationStatus(ApplicationStatusEnum.REJECTED);
            cloudAccountFormRepository.save((CloudAccountFormData) form);
        } else if (formClass.equals(VpnFormData.class)) {
            form = vpnFormRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("VPN başvurusu bulunamadı"));
            form.setRejected(true);
            form.setRejectionReason(reason);
            form.setRejectionDate(LocalDateTime.now());
            form.setApplicationStatus(ApplicationStatusEnum.REJECTED);
            vpnFormRepository.save((VpnFormData) form);
        }
    }

    /**
     * Tüm bekleyen başvuruları getirir
     */
    public List<BaseFormData> getPendingApplications() {
        List<BaseFormData> allPendingApplications = new ArrayList<>();
        
        // Mail başvuruları
        allPendingApplications.addAll(mailFormRepository.findByApplicationStatus(ApplicationStatusEnum.PENDING));
        
        // Eduroam başvuruları
        allPendingApplications.addAll(eduroamFormRepository.findByApplicationStatus(ApplicationStatusEnum.PENDING));
        
        // IP-MAC başvuruları
        allPendingApplications.addAll(ipMacFormRepository.findByApplicationStatus(ApplicationStatusEnum.PENDING));
        
        // GAUN BULUT başvuruları
        allPendingApplications.addAll(cloudAccountFormRepository.findByApplicationStatus(ApplicationStatusEnum.PENDING));
        
        // VPN başvuruları
        allPendingApplications.addAll(vpnFormRepository.findByApplicationStatus(ApplicationStatusEnum.PENDING));
        
        // Başvuruları tarihe göre sırala (en yeni en üstte)
        allPendingApplications.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
        
        return allPendingApplications;
    }

    /**
     * Başvuruyu ID'ye göre getirir
     */
    public BaseFormData getApplicationById(Long id) {
        // Tüm repository'lerde ara
        Optional<? extends BaseFormData> application = Optional.empty();
        
        application = mailFormRepository.findById(id);
        if (application.isPresent()) return application.get();
        
        application = eduroamFormRepository.findById(id);
        if (application.isPresent()) return application.get();
        
        application = ipMacFormRepository.findById(id);
        if (application.isPresent()) return application.get();
        
        application = cloudAccountFormRepository.findById(id);
        if (application.isPresent()) return application.get();
        
        application = vpnFormRepository.findById(id);
        if (application.isPresent()) return application.get();
        
        throw new RuntimeException("Application not found with id: " + id);
    }

    /**
     * Başvuruyu onayla
     */
    public void approveApplication(Long id) {
        BaseFormData application = getApplicationById(id);
        application.setApplicationStatus(ApplicationStatusEnum.APPROVED);
        
        // İlgili repository'ye kaydet
        if (application instanceof MailFormData) {
            mailFormRepository.save((MailFormData) application);
        } else if (application instanceof EduroamFormData) {
            eduroamFormRepository.save((EduroamFormData) application);
        } else if (application instanceof IpMacFormData) {
            ipMacFormRepository.save((IpMacFormData) application);
        } else if (application instanceof CloudAccountFormData) {
            cloudAccountFormRepository.save((CloudAccountFormData) application);
        } else if (application instanceof VpnFormData) {
            vpnFormRepository.save((VpnFormData) application);
        }
    }

    /**
     * Başvuruyu reddet
     */
    public void rejectApplication(Long id) {
        BaseFormData application = getApplicationById(id);
        application.setApplicationStatus(ApplicationStatusEnum.REJECTED);
        
        // İlgili repository'ye kaydet
        if (application instanceof MailFormData) {
            mailFormRepository.save((MailFormData) application);
        } else if (application instanceof EduroamFormData) {
            eduroamFormRepository.save((EduroamFormData) application);
        } else if (application instanceof IpMacFormData) {
            ipMacFormRepository.save((IpMacFormData) application);
        } else if (application instanceof CloudAccountFormData) {
            cloudAccountFormRepository.save((CloudAccountFormData) application);
        } else if (application instanceof VpnFormData) {
            vpnFormRepository.save((VpnFormData) application);
        }
    }
} 