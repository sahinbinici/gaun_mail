package gaun.apply.service.form;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import gaun.apply.entity.form.CloudAccountFormData;
import gaun.apply.repository.form.CloudAccountFormRepository;

@Service
public class CloudAccountFormService {
    private final CloudAccountFormRepository cloudAccountFormRepository;

    public CloudAccountFormService(CloudAccountFormRepository cloudAccountFormRepository) {
        this.cloudAccountFormRepository = cloudAccountFormRepository;
    }

    public CloudAccountFormData findByTcKimlikNo(String tcKimlikNo){
        return cloudAccountFormRepository.findByTcKimlikNo(tcKimlikNo);
    }

    public long countByStatus(boolean status){
        return cloudAccountFormRepository.countByStatus(status);
    }

    public List<CloudAccountFormData> findTop10ByOrderByApplyDateDesc(){
        return cloudAccountFormRepository.findTop10ByOrderByApplyDateDesc();
    }

    public Optional<CloudAccountFormData> findById(long id){
        return cloudAccountFormRepository.findById(id);
    }

    public void saveCloudAccountFormData(CloudAccountFormData cloudAccountFormData){
        cloudAccountFormRepository.save(cloudAccountFormData);
    }

    public List<CloudAccountFormData> getAllCloudAccountForms() {
        return cloudAccountFormRepository.findAll();
    }
}
