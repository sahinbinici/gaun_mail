package gaun.apply.service.form;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import gaun.apply.entity.form.CloudAccountFormData;
import gaun.apply.repository.form.CloudAccountFormRepository;
import gaun.apply.enums.ApplicationStatusEnum;

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

    public void deleteCloudAccountForm(Long id) {
        cloudAccountFormRepository.deleteById(id);
    }

    public List<CloudAccountFormData> findLastMonthApplications() {
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        return cloudAccountFormRepository.findByApplyDateAfter(oneMonthAgo);
    }

    public List<CloudAccountFormData> findLast100Applications() {
        return cloudAccountFormRepository.findTop100ByOrderByApplyDateDesc();
    }

    public List<CloudAccountFormData> findByDurum(String durum) {
        try {
            ApplicationStatusEnum status = ApplicationStatusEnum.valueOf(durum);
            return cloudAccountFormRepository.findByApplicationStatus(status);
        } catch (IllegalArgumentException e) {
            return List.of(); // Return empty list if status is invalid
        }
    }
}
