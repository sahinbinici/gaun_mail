package gaun.apply.domain.common;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import gaun.apply.application.dto.DashboardDataDTO;
import org.springframework.stereotype.Service;

import gaun.apply.application.dto.DashboardDataDTO;
import gaun.apply.application.dto.StudentDto;
import gaun.apply.common.enums.ApplicationStatusEnum;
import gaun.apply.domain.common.BaseFormData;
import gaun.apply.domain.eduroam.entity.EduroamFormData;
import gaun.apply.domain.mail.entity.MailFormData;
import gaun.apply.domain.common.BaseFormRepository;
import gaun.apply.domain.eduroam.repository.EduroamFormRepository;
import gaun.apply.domain.mail.repository.MailFormRepository;
import gaun.apply.domain.user.entity.Staff;
import gaun.apply.domain.user.entity.User;
import gaun.apply.infrastructure.service.SmsService;
import gaun.apply.domain.user.service.StaffService;
import gaun.apply.domain.user.service.StudentService;
import gaun.apply.domain.user.service.UserService;
import gaun.apply.domain.mail.service.MailFormService;
import gaun.apply.domain.eduroam.service.EduroamFormService;

@Service
public class FormService {
    private final Map<String, BaseFormRepository<? extends BaseFormData>> repositories;
    private final SmsService smsService;
    private final StudentService studentService;
    private final StaffService staffService;
    private final MailFormRepository mailFormRepository;
    private final EduroamFormRepository eduroamFormRepository;
    private final UserService userService;
    private final MailFormService mailFormService;
    private final EduroamFormService eduroamFormService;

    public FormService(List<BaseFormRepository<? extends BaseFormData>> repos,
                       SmsService smsService,
                       StudentService studentService,
                       StaffService staffService,
                       MailFormRepository mailFormRepository,
                       EduroamFormRepository eduroamFormRepository,
                       UserService userService,
                       MailFormService mailFormService,
                       EduroamFormService eduroamFormService) {
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
        this.userService = userService;
        this.mailFormService = mailFormService;
        this.eduroamFormService = eduroamFormService;
    }

    /**
     * Admin dashboard için gerekli tüm verileri hazırlar
     */
    public DashboardDataDTO prepareDashboardData(String filter, String tcKimlikNo, String status) {
        DashboardDataDTO dashboardData = new DashboardDataDTO();

        // Form verilerini filtrele
        //List<MailFormData> mailForms = getFilteredMailForms(filter, tcKimlikNo, status);
        List<MailFormData> mailForms = getFilteredMailForms(filter, tcKimlikNo, status);
        mailForms.sort(Comparator.comparing(MailFormData::getApplyDate).reversed());
        List<EduroamFormData> eduroamForms = getFilteredEduroamForms(filter, tcKimlikNo, status);
        eduroamForms.sort(Comparator.comparing(EduroamFormData::getApplyDate).reversed());

        dashboardData.setMailForms(mailForms);
        dashboardData.setEduroamForms(eduroamForms);

        // İstatistikleri hesapla
        dashboardData.setMailStats(getFormStats(mailForms));
        dashboardData.setEduroamStats(getFormStats(eduroamForms));
        dashboardData.setUserStats(getUserStats());

        return dashboardData;
    }

    /**
     * Filtrelenmiş mail formlarını getirir
     */
    private List<MailFormData> getFilteredMailForms(String filter, String tcKimlikNo, String status) {
        if (tcKimlikNo != null && !tcKimlikNo.isEmpty()) {
            MailFormData mailForm = mailFormService.findByTcKimlikNo(tcKimlikNo);
            return mailForm != null ? List.of(mailForm) : new ArrayList<>();
        } else if (status != null && !status.isEmpty()) {
            return mailFormService.findByDurum(status);
        } else if (filter != null) {
            switch (filter) {
                case "pending":
                    return mailFormService.findByDurum("PENDING");
                case "approved":
                    return mailFormService.findByDurum("APPROVED");
                case "rejected":
                    return mailFormService.findByDurum("REJECTED");
                default:
                    return mailFormService.getAllMailForms();
            }
        } else {
            return mailFormService.getAllMailForms();
        }
    }

    /**
     * Filtrelenmiş eduroam formlarını getirir
     */
    private List<EduroamFormData> getFilteredEduroamForms(String filter, String tcKimlikNo, String status) {
        if (tcKimlikNo != null && !tcKimlikNo.isEmpty()) {
            EduroamFormData eduroamForm = eduroamFormService.eduroamFormDataTc(tcKimlikNo);
            return eduroamForm != null ? List.of(eduroamForm) : new ArrayList<>();
        } else if (status != null && !status.isEmpty()) {
            return eduroamFormService.findByDurum(status);
        } else if (filter != null) {
            switch (filter) {
                case "pending":
                    return eduroamFormService.findByDurum("PENDING");
                case "approved":
                    return eduroamFormService.findByDurum("APPROVED");
                case "rejected":
                    return eduroamFormService.findByDurum("REJECTED");
                default:
                    return eduroamFormService.getAllEduroamForms();
            }
        } else {
            return eduroamFormService.getAllEduroamForms();
        }
    }

    /**
     * Form istatistiklerini hesaplar
     */
    private Map<String, Long> getFormStats(List<? extends BaseFormData> forms) {
        Map<String, Long> stats = new HashMap<>();
        if (forms == null || forms.isEmpty()) {
            stats.put("total", 0L);
            stats.put("pending", 0L);
            stats.put("approved", 0L);
            stats.put("rejected", 0L);
            return stats;
        }

        stats.put("total", (long) forms.size());
        stats.put("pending", forms.stream()
                .filter(f -> f.getApplicationStatus() == ApplicationStatusEnum.PENDING)
                .count());
        stats.put("approved", forms.stream()
                .filter(f -> f.getApplicationStatus() == ApplicationStatusEnum.APPROVED)
                .count());
        stats.put("rejected", forms.stream()
                .filter(f -> f.getApplicationStatus() == ApplicationStatusEnum.REJECTED)
                .count());

        return stats;
    }

    /**
     * Kullanıcı istatistiklerini hesaplar
     */
    private Map<String, Long> getUserStats() {
        Map<String, Long> userStats = new HashMap<>();
        // Bu metodun implementasyonu mevcut getUserStats() metodundan alınacak
        // Burada basit bir örnek veriyorum
        userStats.put("totalUsers", 0L);
        userStats.put("activeUsers", 0L);
        userStats.put("students", 0L);
        userStats.put("staff", 0L);
        return userStats;
    }

    /**
     * Kullanıcı detaylarını getirir
     */
    public Map<String, Object> getUserDetails(String username) {
        Map<String, Object> response = new HashMap<>();

        User user = userService.findByTcKimlikNo(username);
        if (user == null) {
            throw new RuntimeException("Kullanıcı bulunamadı");
        }

        // Temel kullanıcı bilgilerini ekle
        response.put("username", user.getIdentityNumber());
        response.put("tcKimlikNo", user.getIdentityNumber());

        // Kullanıcı türünü belirle (öğrenci ID'si 12 haneli sayı olmalı)
        boolean isStudent = user.getIdentityNumber().length() == 12;
        response.put("userType", isStudent ? "student" : "staff");

        // Kullanıcı türüne göre ek bilgileri getir
        if (isStudent) {
            // Öğrenci bilgilerini ekle
            try {
                StudentDto studentDto = studentService.findByOgrenciNo(user.getIdentityNumber());
                MailFormData mailFormData = mailFormService.findByTcKimlikNo(user.getTcKimlikNo());
                if (studentDto != null) {
                    response.put("tcKimlikNo", studentDto.getTcKimlikNo());
                    response.put("ogrenciNo", studentDto.getOgrenciNo());
                    response.put("ad", studentDto.getAd());
                    response.put("soyad", studentDto.getSoyad());
                    response.put("gsm1", studentDto.getGsm1());
                    response.put("eposta1", mailFormData != null ? mailFormData.getEmail() : studentDto.getEposta1());
                    response.put("fakulte", studentDto.getFakKod());
                    response.put("bolumAd", studentDto.getBolumAd());
                    response.put("programAd", studentDto.getProgramAd());
                    response.put("egitimDerecesi", studentDto.getEgitimDerecesi());
                }
            } catch (Exception ex) {
                // Öğrenci bilgileri alınamadıysa hata mesajı ekle
                response.put("error", "Öğrenci bilgileri alınamadı: " + ex.getMessage());
            }
        } else {
            // Personel bilgilerini ekle
            try {
                Staff staff = staffService.findByTcKimlikNo(user.getIdentityNumber());
                MailFormData mailFormData = mailFormService.findByTcKimlikNo(user.getTcKimlikNo());
                if (staff != null) {
                    response.put("tcKimlikNo", staff.getTcKimlikNo());
                    response.put("ad", staff.getAd());
                    response.put("soyad", staff.getSoyad());
                    response.put("gsm", staff.getGsm());
                    response.put("eposta", mailFormData.getEmail() != null ? mailFormData.getEmail() : staff.getEmail());
                    response.put("birim", staff.getCalistigiBirim()); // Doğru alan adı: calistigiBirim
                    response.put("unvan", staff.getUnvan());
                }
            } catch (Exception ex) {
                // Personel bilgileri alınamadıysa hata mesajı ekle
                response.put("error", "Personel bilgileri alınamadı: " + ex.getMessage());
            }
        }

        return response;
    }

    /**
     * Form aktivasyonu işlemini gerçekleştirir
     */
    public String activateForm(String formType, Long id) {
        switch (formType) {
            case "mail":
                return activateMailForm(id);
            case "eduroam":
                return activateEduroamForm(id);
            default:
                throw new IllegalArgumentException("Geçersiz form tipi: " + formType);
        }
    }

    /**
     * Mail form aktivasyonu
     */
    private String activateMailForm(Long id) {
        MailFormData mailForm = mailFormService.findMailFormById(id)
                .orElseThrow(() -> new RuntimeException("Mail başvurusu bulunamadı"));

        mailForm.setStatus(true);
        mailForm.setApplicationStatus(ApplicationStatusEnum.APPROVED);
        mailForm.setApprovalDate(LocalDateTime.now());

        // Öğrenci mi personel mi kontrolü (öğrenci ID'si 12 haneli)
        boolean isStudent = mailForm.getOgrenciNo() != null;

        // Doğru servisi kullanarak e-posta adresi oluşturma
        String emailAddress;
        if (isStudent) {
            // Öğrenci için email oluştur - DTO'dan TC Kimlik No kullan
            emailAddress = studentService.createEmailAddress(mailForm.getOgrenciNo());
        } else {
            // Personel için email oluştur
            emailAddress = staffService.createEmailAddress(mailForm.getTcKimlikNo());
        }

        mailForm.setEmail(emailAddress);
        mailFormService.save(mailForm);

        return "Mail başvurusu onaylandı";
    }

    /**
     * Eduroam form aktivasyonu
     */
    private String activateEduroamForm(Long id) {
        EduroamFormData eduroamForm = eduroamFormService.findById(id)
                .orElseThrow(() -> new RuntimeException("Eduroam başvurusu bulunamadı"));

        eduroamForm.setStatus(true);
        eduroamForm.setApplicationStatus(ApplicationStatusEnum.APPROVED);
        eduroamForm.setApprovalDate(LocalDateTime.now());
        eduroamFormService.saveEduroamFormData(eduroamForm);

        return "Eduroam başvurusu onaylandı";
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
            String tcKimlikNo = mailForm.getTcKimlikNo();
            String ogrenciNo = mailForm.getOgrenciNo();
            // Öğrenci mi personel mi kontrolü (staff/öğrenci ayrımı - öğrenci ID'si 12 haneli)
            boolean isStudent = ogrenciNo != null && ogrenciNo.length() == 12 && ogrenciNo.matches("\\d+");

            try {
                if (isStudent) {
                    // Öğrenci için SMS gönder
                    StudentDto student = studentService.findByOgrenciNo(ogrenciNo);
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

            // Öğrenci mi personel mi kontrolü (staff/öğrenci ayrımı - öğrenci ID'si 12 haneli)
            boolean isStudent = eduroamForm.getOgrenciNo() != null;//username != null && username.length() == 12 && username.matches("\\d+");

            try {
                if (isStudent) {
                    // Öğrenci için SMS gönder
                    StudentDto student = studentService.findByOgrenciNo(eduroamForm.getOgrenciNo());
                    if (student != null && student.getGsm1() != null) {
                        smsService.sendSms(new String[]{student.getGsm1()},
                                "GAÜN Eduroam başvurunuz reddedildi. " + form.getRejectionReason());
                    }
                } else {
                    // Personel için SMS gönder
                    String tcKimlikNo = eduroamForm.getTcKimlikNo();
                    if (tcKimlikNo != null && !tcKimlikNo.isEmpty()) {
                        Staff staff = staffService.findByTcKimlikNo(tcKimlikNo);
                        if (staff != null && staff.getGsm() != null && !staff.getGsm().isEmpty()) {
                            smsService.sendSms(new String[]{staff.getGsm()},
                                    "GAÜN Eduroam başvurunuz reddedildi. " + form.getRejectionReason());
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