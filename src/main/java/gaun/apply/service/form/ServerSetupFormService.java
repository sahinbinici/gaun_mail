package gaun.apply.service.form;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import gaun.apply.entity.form.ServerSetupFormData;
import gaun.apply.repository.form.ServerSetupFormRepository;

@Service
public class ServerSetupFormService {
    private final ServerSetupFormRepository serverSetupFormRepository;

    public ServerSetupFormService(ServerSetupFormRepository serverSetupFormRepository) {
        this.serverSetupFormRepository = serverSetupFormRepository;
    }

    public ServerSetupFormData findByTcKimlikNo(String tcKimlikNo) {
        return serverSetupFormRepository.findByTcKimlikNo(tcKimlikNo);
    }

    public long countByStatus(boolean status) {
        return serverSetupFormRepository.countByStatus(status);
    }

    public List<ServerSetupFormData> findTop10ByOrderByApplyDateDesc() {
        return serverSetupFormRepository.findTop10ByOrderByApplyDateDesc();
    }

    public Optional<ServerSetupFormData> findById(long id) {
        return serverSetupFormRepository.findById(id);
    }

    public void saveServerSetupFormData(ServerSetupFormData serverSetupFormData) {
        serverSetupFormRepository.save(serverSetupFormData);
    }

    public List<ServerSetupFormData> getAllServerSetupForms() {
        return serverSetupFormRepository.findAll();
    }

    public void deleteServerSetupForm(Long id) {
        serverSetupFormRepository.deleteById(id);
    }
} 