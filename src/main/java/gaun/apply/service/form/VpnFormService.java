package gaun.apply.service.form;

import gaun.apply.entity.form.VpnFormData;
import gaun.apply.repository.form.VpnFormRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VpnFormService {
    private final VpnFormRepository vpnFormRepository;

    public VpnFormService(VpnFormRepository vpnFormRepository) {
        this.vpnFormRepository = vpnFormRepository;
    }

    public VpnFormData findByTcKimlikNo(String tcKimlikNo){
        return vpnFormRepository.findByTcKimlikNo(tcKimlikNo);
    }

    public long countByStatus(boolean status){
        return vpnFormRepository.countByStatus(status);
    }

    public List<VpnFormData> findTop10ByOrderByApplyDateDesc(){
        return vpnFormRepository.findTop10ByOrderByApplyDateDesc();
    }

    public Optional<VpnFormData> findById(long id){
        return vpnFormRepository.findById(id);
    }

    public void saveVpnFormData(VpnFormData vpnFormData){
        vpnFormRepository.save(vpnFormData);
    }
}
