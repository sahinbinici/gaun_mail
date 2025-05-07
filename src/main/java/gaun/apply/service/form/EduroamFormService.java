package gaun.apply.service.form;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import gaun.apply.entity.form.EduroamFormData;
import gaun.apply.repository.form.EduroamFormRepository;
import gaun.apply.enums.ApplicationStatusEnum;
import gaun.apply.dto.StudentDto;
import gaun.apply.service.StudentService;

@Service
public class EduroamFormService {
    private final EduroamFormRepository eduroamFormRepository;
    private final StudentService studentService;

    public EduroamFormService(EduroamFormRepository eduroamFormRepository, StudentService studentService) {
        this.eduroamFormRepository = eduroamFormRepository;
        this.studentService = studentService;
    }

    public EduroamFormData eduroamFormData(String username){
        return eduroamFormRepository.findByUsername(username);
    }
    
    public EduroamFormData findByTcKimlikNo(String tcKimlikNo) {
        return eduroamFormRepository.findByTcKimlikNo(tcKimlikNo);
    }

    public List<EduroamFormData> findTop10ByOrderByApplyDateDesc(){
        return eduroamFormRepository.findTop10ByOrderByApplyDateDesc();
    }
    
    public List<EduroamFormData> findLast100Applications() {
        return eduroamFormRepository.findTop100ByOrderByApplyDateDesc();
    }
    
    public List<EduroamFormData> findLastMonthApplications() {
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        return eduroamFormRepository.findByApplyDateAfter(oneMonthAgo);
    }

    public void saveEduroamFormData(EduroamFormData eduroamFormData){
        eduroamFormRepository.save(eduroamFormData);
    }

    public Optional<EduroamFormData> findById(Long id) {
        return eduroamFormRepository.findById(id);
    }

    public List<EduroamFormData> getAllEduroamForms() {
        return eduroamFormRepository.findAll();
    }

    public void deleteEduroamForm(Long id) {
        eduroamFormRepository.deleteById(id);
    }

    public List<EduroamFormData> findByDurum(String durum) {
        return eduroamFormRepository.findByApplicationStatus(ApplicationStatusEnum.valueOf(durum));
    }

    /**
     * Returns a list of pending eduroam applications in text format.
     * Format: tcKimlikNo#ogrenciNo#ad#soyad#fakülte#bölüm#gsm1#e-posta1
     * 
     * @return String containing all pending applications in the specified format
     */
    public String getPendingApplicationsAsText() {
        // Get applications that are actually pending (not approved and not rejected)
        List<EduroamFormData> allApplications = eduroamFormRepository.findAll();
        List<EduroamFormData> pendingApplications = new java.util.ArrayList<>();
        
        for (EduroamFormData application : allApplications) {
            if (!application.isStatus() && !application.isRejected()) {
                pendingApplications.add(application);
            }
        }
        
        StringBuilder result = new StringBuilder();
        
        for (EduroamFormData application : pendingApplications) {
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
            String email = "";
            
            // If student information is available, use it
            if (student != null) {
                ad = student.getAd() != null ? student.getAd() : "";
                soyad = student.getSoyad() != null ? student.getSoyad() : "";
                fakulte = student.getFakKod() != null ? student.getFakKod() : "";
                bolum = student.getBolumAd() != null ? student.getBolumAd() : "";
                gsm1 = student.getGsm1() != null ? student.getGsm1() : "";
                email = student.getEposta1() != null ? student.getEposta1() : "";
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
