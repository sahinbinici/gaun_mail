package gaun.apply.service.form;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import gaun.apply.entity.form.MailFormData;
import gaun.apply.repository.form.MailFormRepository;
import gaun.apply.enums.ApplicationStatusEnum;
import gaun.apply.dto.StudentDto;
import gaun.apply.service.StudentService;

@Service
public class MailFormService {
    private final MailFormRepository mailFormRepository;
    private final StudentService studentService;

    public MailFormService(MailFormRepository mailFormRepository, StudentService studentService) {
        this.mailFormRepository = mailFormRepository;
        this.studentService = studentService;
    }

    public Optional<MailFormData> findMailFormById(Long id) {
        return mailFormRepository.findById(id);
    }

    public MailFormData findByUsername(String username) {
        return mailFormRepository.findByUsername(username);
    }

    public MailFormData findByTcKimlikNo(String tcKimlikNo) {
        return mailFormRepository.findByTcKimlikNo(tcKimlikNo);
    }

    public List<MailFormData> findTop10ByOrderByApplyDateDesc() {
        return mailFormRepository.findTop10ByOrderByApplyDateDesc();
    }

    public List<MailFormData> findLast100Applications() {
        return mailFormRepository.findTop100ByOrderByApplyDateDesc();
    }

    public List<MailFormData> findLastMonthApplications() {
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        return mailFormRepository.findByApplyDateAfter(oneMonthAgo);
    }

    public void deleteMailForm(Long id) {
        mailFormRepository.deleteById(id);
    }

    public List<MailFormData> findByDurum(String durum) {
        try {
            ApplicationStatusEnum status = ApplicationStatusEnum.valueOf(durum.toUpperCase());
            return findByDurum(status);
        } catch (IllegalArgumentException e) {
            return new ArrayList<>();
        }
    }

    public List<MailFormData> findByDurum(ApplicationStatusEnum status) {
        return mailFormRepository.findByApplicationStatus(status);
    }

    public List<MailFormData> getAllMailForms() {
        return mailFormRepository.findAll();
    }

    public MailFormData save(MailFormData mailFormData) {
        return mailFormRepository.save(mailFormData);
    }

    /**
     * Returns a list of pending mail applications in text format.
     * Format: tcKimlikNo#ogrenciNo#ad#soyad#fakülte#bölüm#gsm1#e-posta1
     * 
     * @return String containing all pending applications in the specified format
     */
    public String getPendingApplicationsAsText() {
        // Get applications that are actually pending (not approved and not rejected)
        List<MailFormData> allApplications = mailFormRepository.findAll();
        List<MailFormData> pendingApplications = new ArrayList<>();
        
        for (MailFormData application : allApplications) {
            if (!application.isStatus() && !application.isRejected()) {
                pendingApplications.add(application);
            }
        }
        
        StringBuilder result = new StringBuilder();
        
        for (MailFormData application : pendingApplications) {
            // Get TC Kimlik No
            String tcKimlikNo = application.getTcKimlikNo();
            String ogrenciNo = application.getUsername() != null ? application.getUsername() : "";
            
            // Try to get student information from StudentService
            StudentDto student = studentService.findByOgrenciNo(ogrenciNo);
            
            String ad = "";
            String soyad = "";
            String fakulte = "";
            String bolum = "";
            String gsm1 = "";
            String email = application.getEmail() != null ? application.getEmail() : "";
            
            // If student information is available, use it
            if (student != null) {
                ad = student.getAd() != null ? student.getAd() : "";
                soyad = student.getSoyad() != null ? student.getSoyad() : "";
                fakulte = student.getFakKod() != null ? student.getFakKod() : "";
                bolum = student.getBolumAd() != null ? student.getBolumAd() : "";
                gsm1 = student.getGsm1() != null ? student.getGsm1() : "";
                
                // If email is empty in application but available in student, use it
                if (email.isEmpty() && student.getEposta1() != null) {
                    email = student.getEposta1();
                }
            }
            
            // Append the data in the required format
            result.append(tcKimlikNo).append("#")
                  .append(ogrenciNo).append("#")
                  .append(ad).append("#")
                  .append(soyad).append("#")
                  .append(fakulte).append("#")
                  .append(bolum).append("#")
                  .append(gsm1).append("#")
                  .append(email)
                  .append("\n");
        }
        
        return result.toString();
    }
}
