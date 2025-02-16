package gaun.apply.service.form;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import gaun.apply.entity.form.IpMacFormData;
import gaun.apply.repository.form.IpMacFormRepository;

@Service
public class IpMacFormService {
    private final IpMacFormRepository ipMacFormRepository;

    public IpMacFormService(IpMacFormRepository ipMacFormRepository) {
        this.ipMacFormRepository = ipMacFormRepository;
    }

    public IpMacFormData findByTcKimlikNo(String tcKimlikNo){
        return ipMacFormRepository.findByTcKimlikNo(tcKimlikNo);
    }

    public long countByStatus(boolean status){
        return ipMacFormRepository.countByStatus(status);
    }

    public List<IpMacFormData> findTop10ByOrderByApplyDateDesc(){
        return ipMacFormRepository.findTop10ByOrderByApplyDateDesc();
    }

    public Optional<IpMacFormData> findById(long id){
        return ipMacFormRepository.findById(id);
    }

    public void saveIpMacFormData(IpMacFormData ipMacFormData){
        ipMacFormRepository.save(ipMacFormData);
    }

    public List<IpMacFormData> getAllIpMacForms() {
        return ipMacFormRepository.findAll();
    }
}
