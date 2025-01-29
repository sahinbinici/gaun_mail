package gaun.apply.service.form;

import gaun.apply.entity.form.WirelessNetworkFormData;
import gaun.apply.repository.form.WirelessNetworkFormRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WirelessNetworkFormService {
    private final WirelessNetworkFormRepository wirelessNetworkFormRepository;

    public WirelessNetworkFormService(WirelessNetworkFormRepository wirelessNetworkFormRepository) {
        this.wirelessNetworkFormRepository = wirelessNetworkFormRepository;
    }

    public WirelessNetworkFormData findByTcKimlikNo(String tcKimlikNo){
        return wirelessNetworkFormRepository.findByTcKimlikNo(tcKimlikNo);
    }

    public long countByStatus(boolean status){
        return wirelessNetworkFormRepository.countByStatus(status);
    }

    public List<WirelessNetworkFormData> findTop10ByOrderByApplyDateDesc(){
        return wirelessNetworkFormRepository.findTop10ByOrderByApplyDateDesc();
    }

    public Optional<WirelessNetworkFormData> findById(long id){
        return wirelessNetworkFormRepository.findById(id);
    }

    public void saveWirelessNetworkFormData(WirelessNetworkFormData wirelessNetworkFormData){
        wirelessNetworkFormRepository.save(wirelessNetworkFormData);
    }
}
