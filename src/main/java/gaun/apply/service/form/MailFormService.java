package gaun.apply.service.form;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import gaun.apply.entity.form.MailFormData;
import gaun.apply.repository.form.MailFormRepository;
import gaun.apply.enums.ApplicationStatusEnum;

@Service
public class MailFormService {
    private final MailFormRepository mailFormRepository;

    public MailFormService(MailFormRepository mailFormRepository) {
        this.mailFormRepository = mailFormRepository;
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
}
