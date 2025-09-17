package gaun.apply.domain.eduroam.service;

import java.time.Clock;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

import gaun.apply.application.dto.EduroamFormDto;
import gaun.apply.application.dto.StaffDto;
import gaun.apply.application.dto.StudentDto;
import gaun.apply.common.util.TurkishCharReplace;
import gaun.apply.domain.user.service.StaffService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import gaun.apply.domain.eduroam.entity.EduroamFormData;
import gaun.apply.domain.eduroam.repository.EduroamFormRepository;
import gaun.apply.common.enums.ApplicationStatusEnum;
import gaun.apply.domain.user.service.StudentService;

@Service
public class EduroamFormService {
    private final EduroamFormRepository eduroamFormRepository;
    private final StudentService studentService;
    private final StaffService staffService;
    private final ModelMapper modelMapper;
    private final Clock clock;

    public EduroamFormService(EduroamFormRepository eduroamFormRepository, StudentService studentService, StaffService staffService, ModelMapper modelMapper, Clock clock) {
        this.clock = clock;
        this.eduroamFormRepository = eduroamFormRepository;
        this.studentService = studentService;
        this.staffService = staffService;
        this.modelMapper = modelMapper;
    }

    public EduroamFormData eduroamFormData(String tcKimlikNo) {
        return eduroamFormRepository.findByTcKimlikNo(tcKimlikNo);
    }
    
    public EduroamFormData findByTcKimlikNo(String tcKimlikNo) {
        return eduroamFormRepository.findByTcKimlikNo(tcKimlikNo);
    }
    
    public EduroamFormData eduroamFormDataTc(String tcKimlikNo) {
        return findByTcKimlikNo(tcKimlikNo);
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

    public static StringBuilder getEduroamStringBuilder(List<EduroamFormData> pendingForms) {
        StringBuilder sb = new StringBuilder();
        for (EduroamFormData form : pendingForms) {
            sb.append(form.getTcKimlikNo()).append("#")
                    .append(form.getOgrenciNo()!=null ? form.getOgrenciNo() : form.getSicilNo()).append("#")
                    .append(form.getAd()).append("#")
                    .append(form.getSoyad()).append("#")
                    .append(form.getOgrenciNo()!=null ? form.getFakulte() : form.getCalistigiBirim()).append("#")
                    .append(form.getOgrenciNo()!= null ? form.getBolum() : form.getUnvan()).append("#")
                    .append(form.getGsm1()).append("#")
                    .append(form.getEmail())
                    .append(form.getOgrenciNo()==null ? form.getDogumTarihi() : "").append("#")
                    .append(form.getPassword())
                    .append("\n");
        }
        TurkishCharReplace.replaceTurkishChars(sb);
        return sb;
    }

    public void saveEduroamApply(EduroamFormDto eduroamFormDto) {
        StaffDto staffDto;
        StudentDto studentDto;
        EduroamFormData eduroamFormData = new EduroamFormData();
        if(eduroamFormDto.getOgrenciNo()!=null && !eduroamFormDto.getOgrenciNo().isEmpty()) {
            studentDto=studentService.findByOgrenciNo(eduroamFormDto.getOgrenciNo());
            eduroamFormData.setOgrenciNo(studentDto.getOgrenciNo());
            eduroamFormData.setAd(studentDto.getAd());
            eduroamFormData.setSoyad(studentDto.getSoyad());
            eduroamFormData.setEmail(studentDto.getEposta1());
            eduroamFormData.setTcKimlikNo(eduroamFormDto.getTcKimlikNo());
            eduroamFormData.setFakulte(studentDto.getFakKod());
            eduroamFormData.setBolum(studentDto.getBolumAd());
            eduroamFormData.setGsm1(studentDto.getGsm1());
        }else {
            staffDto=staffService.findStaffDtoByTcKimlikNo(eduroamFormDto.getTcKimlikNo());
            eduroamFormData.setTcKimlikNo(String.valueOf(staffDto.getTcKimlikNo()));
            eduroamFormData.setAd(staffDto.getAd());
            eduroamFormData.setSoyad(staffDto.getSoyad());
            eduroamFormData.setSicilNo(String.valueOf(staffDto.getSicilNo()));
            eduroamFormData.setGsm(String.valueOf(staffDto.getGsm()));
            eduroamFormData.setEmail(staffDto.getEmail());
            eduroamFormData.setDogumTarihi(staffDto.getDogumTarihi());
            eduroamFormData.setCalistigiBirim(staffDto.getCalistigiBirim());
            eduroamFormData.setUnvan(staffDto.getUnvan());
        }
        eduroamFormData.setPassword(eduroamFormDto.getPassword());
        eduroamFormData.setStatus(false); // Başlangıçta onaylanmamış
        eduroamFormData.setApplicationStatus(ApplicationStatusEnum.PENDING); // Başlangıçta beklemede
        LocalDateTime now = LocalDateTime.now(clock);
        eduroamFormData.setApplyDate(now);
        eduroamFormData.setCreatedAt(now);
        eduroamFormRepository.save(eduroamFormData);
    }
    /*
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
    */
}
