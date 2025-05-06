package gaun.apply.service.form;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import gaun.apply.enums.ApplicationStatusEnum;

import gaun.apply.entity.form.VpnFormData;
import gaun.apply.repository.form.VpnFormRepository;

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
    
    public List<VpnFormData> findLast100Applications() {
        return vpnFormRepository.findTop100ByOrderByApplyDateDesc();
    }
    
    public List<VpnFormData> findLastMonthApplications() {
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        return vpnFormRepository.findByApplyDateAfter(oneMonthAgo);
    }

    public Optional<VpnFormData> findById(long id){
        return vpnFormRepository.findById(id);
    }

    public void saveVpnFormData(VpnFormData vpnFormData){
        vpnFormRepository.save(vpnFormData);
    }

    public List<VpnFormData> getAllVpnForms() {
        return vpnFormRepository.findAll();
    }

    public void deleteVpnForm(Long id) {
        vpnFormRepository.deleteById(id);
    }

    public List<VpnFormData> findByDurum(String durum) {
        return vpnFormRepository.findByApplicationStatus(ApplicationStatusEnum.valueOf(durum));
    }
}
