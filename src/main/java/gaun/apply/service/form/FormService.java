package gaun.apply.service.form;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import gaun.apply.entity.form.BaseFormData;
import gaun.apply.entity.form.CloudAccountFormData;
import gaun.apply.entity.form.EduroamFormData;
import gaun.apply.entity.form.IpMacFormData;
import gaun.apply.entity.form.MailFormData;
import gaun.apply.entity.form.VpnFormData;
import gaun.apply.repository.form.BaseFormRepository;

@Service
public class FormService {
    private final Map<String, BaseFormRepository<? extends BaseFormData>> repositories;
    private final MailFormService mailFormService;
    private final EduroamFormService eduroamFormService;
    private final VpnFormService vpnFormService;
    private final CloudAccountFormService cloudAccountFormService;
    private final IpMacFormService ipMacFormService;
    
    public FormService(List<BaseFormRepository<? extends BaseFormData>> repos,
                      MailFormService mailFormService,
                      EduroamFormService eduroamFormService,
                      VpnFormService vpnFormService,
                      CloudAccountFormService cloudAccountFormService,
                      IpMacFormService ipMacFormService) {
        repositories = repos.stream()
            .collect(Collectors.toMap(
                r -> r.getClass().getInterfaces()[0].getSimpleName(),
                r -> r
            ));
        this.mailFormService = mailFormService;
        this.eduroamFormService = eduroamFormService;
        this.vpnFormService = vpnFormService;
        this.cloudAccountFormService = cloudAccountFormService;
        this.ipMacFormService = ipMacFormService;
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
        form.setStatus(true);
        form.setApprovalDate(LocalDateTime.now());
        repository.save(form);
    }

    public void rejectForm(Long id, Class<? extends BaseFormData> formClass, String reason) {
        BaseFormData form = null; // form değişkenini burada tanımlayın

        if (formClass.equals(MailFormData.class)) {
            form = mailFormService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Mail başvurusu bulunamadı"));
            form.setRejected(true);
            form.setRejectionReason(reason);
            form.setRejectionDate(LocalDateTime.now());
            mailFormService.saveMailFormData((MailFormData) form);
        } else if (formClass.equals(EduroamFormData.class)) {
            form = eduroamFormService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Eduroam başvurusu bulunamadı"));
            form.setRejected(true);
            form.setRejectionReason(reason);
            form.setRejectionDate(LocalDateTime.now());
            eduroamFormService.saveEduroamFormData((EduroamFormData) form);
        } else if (formClass.equals(IpMacFormData.class)) {
            form = ipMacFormService.findById(id)
                    .orElseThrow(() -> new RuntimeException("IP-MAC başvurusu bulunamadı"));
            form.setRejected(true);
            form.setRejectionReason(reason);
            form.setRejectionDate(LocalDateTime.now());
            ipMacFormService.saveIpMacFormData((IpMacFormData) form);
        } else if (formClass.equals(CloudAccountFormData.class)) {
            form = cloudAccountFormService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Cloud başvurusu bulunamadı"));
            form.setRejected(true);
            form.setRejectionReason(reason);
            form.setRejectionDate(LocalDateTime.now());
            cloudAccountFormService.saveCloudAccountFormData((CloudAccountFormData) form);
        } else if (formClass.equals(VpnFormData.class)) {
            form = vpnFormService.findById(id)
                    .orElseThrow(() -> new RuntimeException("VPN başvurusu bulunamadı"));
            form.setRejected(true);
            form.setRejectionReason(reason);
            form.setRejectionDate(LocalDateTime.now());
            vpnFormService.saveVpnFormData((VpnFormData) form);
        }
    }

    private BaseFormRepository<? extends BaseFormData> getFormRepository(Class<? extends BaseFormData> formClass) {
        String repoName = formClass.getSimpleName().replace("Data", "Repository");
        BaseFormRepository<? extends BaseFormData> repository = repositories.get(repoName);
        if (repository == null) {
            throw new RuntimeException("Repository bulunamadı: " + repoName);
        }
        return repository;
    }
} 