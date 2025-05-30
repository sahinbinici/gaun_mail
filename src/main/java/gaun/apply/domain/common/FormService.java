package gaun.apply.domain.common;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import gaun.apply.application.dto.StudentDto;
import gaun.apply.common.enums.ApplicationStatusEnum;
import gaun.apply.domain.common.BaseFormData;
import gaun.apply.domain.eduroam.entity.EduroamFormData;
import gaun.apply.domain.mail.entity.MailFormData;
import gaun.apply.domain.common.BaseFormRepository;
import gaun.apply.domain.eduroam.repository.EduroamFormRepository;
import gaun.apply.domain.mail.repository.MailFormRepository;
import gaun.apply.domain.user.entity.Staff;
import gaun.apply.infrastructure.service.SmsService;
import gaun.apply.domain.user.service.StaffService;
import gaun.apply.domain.user.service.StudentService;

@Service
public class FormService {
    private final Map<String, BaseFormRepository<? extends BaseFormData>> repositories;
    private final SmsService smsService;
    private final StudentService studentService;
    private final StaffService staffService;
    private final MailFormRepository mailFormRepository;
    private final EduroamFormRepository eduroamFormRepository;
    
    public FormService(List<BaseFormRepository<? extends BaseFormData>> repos,
                       SmsService smsService, 
                       StudentService studentService,
                       StaffService staffService,
                       MailFormRepository mailFormRepository,
                       EduroamFormRepository eduroamFormRepository) {
        repositories = repos.stream()
            .collect(Collectors.toMap(
                r -> r.getClass().getInterfaces()[0].getSimpleName(),
                r -> r
            ));
        this.smsService = smsService;
        this.studentService = studentService;
        this.staffService = staffService;
        this.mailFormRepository = mailFormRepository;
        this.eduroamFormRepository = eduroamFormRepository;
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
            
            // Check if it's a student or staff user
            MailFormData mailForm = (MailFormData) form;
            String username = mailForm.getUsername();
            String tcKimlikNo = mailForm.getTcKimlikNo();
            
            // Öğrenci mi personel mi kontrolü (staff/öğrenci ayrımı - öğrenci ID'si 12 haneli)
            boolean isStudent = username != null && username.length() == 12 && username.matches("\\d+");
            
            try {
                if (isStudent) {
                    // Öğrenci için SMS gönder
                    StudentDto student = studentService.findByOgrenciNo(username);
                    if (student != null && student.getGsm1() != null) {
                        smsService.sendSms(new String[]{student.getGsm1()}, 
                                "GAÜN E-posta başvurunuz reddedildi. Red Sebebi: " + form.getRejectionReason());
                    }
                } else {
                    // Personel için SMS gönder
                    if (tcKimlikNo != null && !tcKimlikNo.isEmpty()) {
                        Staff staff = staffService.findByTcKimlikNo(tcKimlikNo);
                        if (staff != null && staff.getGsm() != null && !staff.getGsm().isEmpty()) {
                            smsService.sendSms(new String[]{staff.getGsm()}, 
                                    "GAÜN E-posta başvurunuz reddedildi. Red Sebebi: " + form.getRejectionReason());
                            System.out.println("Personel " + staff.getAd() + " " + staff.getSoyad() + " için SMS gönderildi");
                        } else {
                            System.out.println("Personel için telefon numarası bulunamadı: " + tcKimlikNo);
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Mail formu SMS gönderiminde hata: " + e.getMessage());
                // SMS gönderiminde hata olsa bile işlemi iptal etmiyoruz
            }
        } else if (formClass.equals(EduroamFormData.class)) {
            form = eduroamFormRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Eduroam başvurusu bulunamadı"));
            form.setRejected(true);
            form.setRejectionReason(reason);
            form.setRejectionDate(LocalDateTime.now());
            form.setApplicationStatus(ApplicationStatusEnum.REJECTED);
            eduroamFormRepository.save((EduroamFormData) form);
            
            // Check if it's a student or staff user
            EduroamFormData eduroamForm = (EduroamFormData) form;
            String username = eduroamForm.getUsername();
            
            // Öğrenci mi personel mi kontrolü (staff/öğrenci ayrımı - öğrenci ID'si 12 haneli)
            boolean isStudent = username != null && username.length() == 12 && username.matches("\\d+");
            
            try {
                if (isStudent) {
                    // Öğrenci için SMS gönder
                    StudentDto student = studentService.findByOgrenciNo(username);
                    if (student != null && student.getGsm1() != null) {
                        smsService.sendSms(new String[]{student.getGsm1()}, 
                                "GAÜN Eduroam başvurunuz reddedildi. Red Sebebi: " + form.getRejectionReason());
                    }
                } else {
                    // Personel için SMS gönder
                    String tcKimlikNo = eduroamForm.getTcKimlikNo();
                    if (tcKimlikNo != null && !tcKimlikNo.isEmpty()) {
                        Staff staff = staffService.findByTcKimlikNo(tcKimlikNo);
                        if (staff != null && staff.getGsm() != null && !staff.getGsm().isEmpty()) {
                            smsService.sendSms(new String[]{staff.getGsm()}, 
                                    "GAÜN Eduroam başvurunuz reddedildi. Red Sebebi: " + form.getRejectionReason());
                            System.out.println("Personel " + staff.getAd() + " " + staff.getSoyad() + " için SMS gönderildi");
                        } else {
                            System.out.println("Personel için telefon numarası bulunamadı: " + tcKimlikNo);
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("SMS gönderiminde hata: " + e.getMessage());
                // SMS gönderiminde hata olsa bile işlemi iptal etmiyoruz
            }
        }
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
        }
    }
}
