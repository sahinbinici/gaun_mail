package gaun.apply.service.form;

import gaun.apply.entity.form.MailFormData;
import gaun.apply.repository.form.MailFormRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MailFormService {
    private final MailFormRepository mailFormRepository;

    public MailFormService(MailFormRepository mailFormRepository) {
        this.mailFormRepository = mailFormRepository;
    }

    public Optional<MailFormData> findById(long id){
        return mailFormRepository.findById(id);
    }

    public MailFormData mailFormData(String username){
        return mailFormRepository.findByUsername(username);
    }
    public long countByStatus(boolean status){
        return mailFormRepository.countByStatus(status);
    }

    public List<MailFormData> findTop10ByOrderByApplyDateDesc(){
        return mailFormRepository.findTop10ByOrderByApplyDateDesc();
    }

    public void saveMailFormData(MailFormData mailFormData){
        mailFormRepository.save(mailFormData);
    }
}
