package gaun.apply.service.form;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

import gaun.apply.dto.StaffDto;
import gaun.apply.entity.Staff;
import gaun.apply.service.StaffService;
import org.modelmapper.ModelMapper;
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
    private final StaffService staffService;
    private final ModelMapper modelMapper;

    public EduroamFormService(EduroamFormRepository eduroamFormRepository, StudentService studentService, StaffService staffService, ModelMapper modelMapper) {
        this.eduroamFormRepository = eduroamFormRepository;
        this.studentService = studentService;
        this.staffService = staffService;
        this.modelMapper = modelMapper;
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
    public String getPendingApplicationsAsText() {
        // Get applications that are actually pending (not approved and not rejected)
        List<EduroamFormData> allApplications = eduroamFormRepository.findAll();
        List<EduroamFormData> pendingApplications = new java.util.ArrayList<>();
        StudentDto studentDto = new StudentDto();
        StaffDto staffDto = new StaffDto();
        Staff staff = new Staff();
        
        for (EduroamFormData application : allApplications) {
            if (!application.isStatus() && !application.isRejected()) {
                pendingApplications.add(application);
            }
        }
        StringBuilder result = new StringBuilder();
        
        for (EduroamFormData application : pendingApplications) {
            String tcKimlikNo = application.getTcKimlikNo();
            String ogrenciNo = application.getUsername() != null ? application.getUsername() : "";
            
            // Try to get studentDto information from StudentService
            if(ogrenciNo.length() == 12){
                studentDto = studentService.findByOgrenciNo(ogrenciNo);
                result.append(studentDto.getTcKimlikNo() != null ? studentDto.getTcKimlikNo() : "--").append("#")
                        .append(studentDto.getOgrenciNo() != null ? studentDto.getOgrenciNo() : "--").append("#")
                        .append(studentDto.getAd() != null ? studentDto.getAd() : "--").append("#")
                        .append(studentDto.getSoyad() != null ? studentDto.getSoyad() : "--").append("#")
                        .append(studentDto.getFakKod() != null ? studentDto.getFakKod() : "--").append("#")
                        .append(studentDto.getBolumAd() != null ? studentDto.getBolumAd() : "--").append("#")
                        .append(studentDto.getGsm1() != null ? studentDto.getGsm1() : "--").append("#")
                        .append(studentDto.getEposta1() != null ? studentDto.getEposta1() : "--")
                        .append(System.lineSeparator());
            }else{
                staff = staffService.findByTcKimlikNo(tcKimlikNo);
                staffDto = modelMapper.map(staff, StaffDto.class);
                result.append(staffDto.getTcKimlikNo() != null ? staffDto.getTcKimlikNo() : "--").append("#")
                        .append(staffDto.getTcKimlikNo() != null ? staffDto.getTcKimlikNo() : "--").append("#")
                        .append(staffDto.getSicilNo() != null ? staffDto.getSicilNo() : "--").append("#")
                        .append(staffDto.getAd() != null ? staffDto.getAd() : "--").append("#")
                        .append(staffDto.getSoyad() != null ? staffDto.getSoyad() : "--").append("#")
                        .append(staffDto.getCalistigiBirim() != null ? staffDto.getCalistigiBirim() : "--").append("#")
                        .append(staffDto.getUnvan() != null ? staffDto.getUnvan() : "--").append("#")
                        .append(staffDto.getGsm() != null ? staffDto.getGsm() : "--").append("#")
                        .append(staffDto.getEmail() != null ? staffDto.getEmail() : "--").append("#")
                        .append(staffDto.getDogumTarihi() != null ? staffDto.getDogumTarihi() : "--")
                        .append(System.lineSeparator());
            }
        }
        return result.toString();
    }
}
