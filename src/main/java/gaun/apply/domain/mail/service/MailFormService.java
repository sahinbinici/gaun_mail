package gaun.apply.domain.mail.service;

import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

import gaun.apply.application.dto.MailFormDto;
import gaun.apply.application.dto.StaffDto;
import gaun.apply.application.dto.StudentDto;
import gaun.apply.common.enums.ApplicationStatusEnum;
import gaun.apply.common.util.RandomPasswordGenerator;
import gaun.apply.common.util.TurkishCharReplace;
import gaun.apply.domain.user.service.StudentService;
import gaun.apply.domain.user.service.StaffService;
import gaun.apply.domain.mail.entity.MailFormData;
import gaun.apply.domain.mail.repository.MailFormRepository;
import org.springframework.stereotype.Service;

@Service
public class MailFormService {
    private final MailFormRepository mailFormRepository;
    private final StudentService studentService;
    private final StaffService staffService;
    private final Clock clock;

    public MailFormService(MailFormRepository mailFormRepository, StudentService studentService, StaffService staffService, Clock clock) {
        this.clock = clock;
        this.staffService = staffService;
        this.mailFormRepository = mailFormRepository;
        this.studentService = studentService;
    }

    public Optional<MailFormData> findMailFormById(Long id) {
        return mailFormRepository.findById(id);
    }

    public MailFormData findByTcKimlikNo(String tcKimlikNo) {
        return mailFormRepository.findByTcKimlikNo(tcKimlikNo);
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

    public static StringBuilder getMailStringBuilder(List<MailFormData> pendingForms) {
        StringBuilder sb = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        for (MailFormData form : pendingForms) {
            sb.append(form.getMailKullaniciAdi()).append(":")
                    .append(form.getAd()).append(".").append(form.getSoyad()).append(":")
                    .append(form.getOgrenciNo()!=null ? form.getOgrenciNo() : form.getSicil()).append(":")
                    .append(form.getOgrenciNo()!=null ? form.getBolum() : form.getUnvan()).append(":")
                    .append(form.getGsm1()).append(":")
                    .append(form.getApplyDate().toLocalDate().format(formatter)).append(":")
                    .append(form.getPassword())
                    .append("\n");
        }
        TurkishCharReplace.replaceTurkishChars(sb);
        return sb;
    }

    /*
    public static StringBuilder getMailStringBuilder(List<MailFormData> pendingForms) {
        StringBuilder sb = new StringBuilder();
        for (MailFormData form : pendingForms) {
            sb.append(form.getTcKimlikNo()).append("#")
                    .append(form.getOgrenciNo()!=null ? form.getOgrenciNo() : form.getSicil()).append("#")
                    .append(form.getAd()).append("#")
                    .append(form.getSoyad()).append("#")
                    .append(form.getOgrenciNo() !=null ? form.getFakulte() : form.getCalistigiBirim()).append("#")
                    .append(form.getOgrenciNo()!=null ? form.getBolum() : form.getUnvan()).append("#")
                    .append(form.getGsm1()).append("#")
                    .append(form.getEmail())
                    .append(form.getSicil()!=null ? form.getDogumTarihi() : "").append("#")
                    .append(form.getPassword())
                    .append("\n");
        }
        TurkishCharReplace.replaceTurkishChars(sb);
        return sb;
    }
*/
    public void saveMailApply(MailFormDto mailFormDto) {
        StaffDto staffDto;
        StudentDto studentDto;
        MailFormData mailFormData = new MailFormData();
        // For students, username is typically a 12-digit student number
        if ((!mailFormDto.getOgrenciNo().isEmpty() || mailFormDto.getFakulteAd()!=null)) {
            studentDto=studentService.findByOgrenciNo(mailFormDto.getOgrenciNo());
            mailFormData.setOgrenciNo(studentDto.getOgrenciNo());
            mailFormData.setAd(studentDto.getAd());
            mailFormData.setSoyad(studentDto.getSoyad());
            mailFormData.setFakkod(studentDto.getFakKod());
            mailFormData.setBolum(studentDto.getBolumAd());
            mailFormData.setGsm(studentDto.getGsm1());
            mailFormData.setTcKimlikNo(mailFormDto.getTcKimlikNo());
            mailFormData.setEmail(studentService.createEmailAddress(mailFormDto.getOgrenciNo()).toLowerCase()+"@mail2.gantep.edu.tr");
            mailFormData.setMailKullaniciAdi(studentService.createEmailAddress(mailFormDto.getOgrenciNo().toLowerCase()));
        }else {
            staffDto=staffService.findStaffDtoByTcKimlikNo(mailFormDto.getTcKimlikNo());
            mailFormData.setTcKimlikNo(mailFormDto.getTcKimlikNo());
            mailFormData.setSicil(staffDto.getSicilNo());
            mailFormData.setAd(staffDto.getAd());
            mailFormData.setSoyad(staffDto.getSoyad());
            mailFormData.setCalistigiBirim(staffDto.getCalistigiBirim());
            mailFormData.setUnvan(staffDto.getUnvan());
            mailFormData.setGsm(String.valueOf(staffDto.getGsm()));
            mailFormData.setEmail(mailFormDto.getEmail().toLowerCase()+"@gantep.edu.tr");
            mailFormData.setDogumTarihi(staffDto.getDogumTarihi());
        }
        mailFormData.setPassword(RandomPasswordGenerator.rastgeleSifreUret(8));
        mailFormData.setStatus(false); // Başlangıçta onaylanmamış
        mailFormData.setApplicationStatus(ApplicationStatusEnum.PENDING); // Başlangıçta beklemede
        LocalDateTime now = LocalDateTime.now(clock);
        mailFormData.setApplyDate(now);
        mailFormData.setCreatedAt(now);
        mailFormRepository.save(mailFormData);
    }

}
