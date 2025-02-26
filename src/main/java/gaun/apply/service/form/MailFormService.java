package gaun.apply.service.form;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import gaun.apply.entity.form.MailFormData;
import gaun.apply.repository.form.MailFormRepository;

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

    public List<MailFormData> findTop10ByOrderByApplyDateDesc(){
        return mailFormRepository.findTop10ByOrderByApplyDateDesc();
    }

    public void saveMailFormData(MailFormData mailFormData){
        mailFormRepository.save(mailFormData);
    }

    public List<MailFormData> getAllMailForms() {
        return mailFormRepository.findAll();
    }

    public void deleteMailForm(Long id) {
        mailFormRepository.deleteById(id);
    }
}
