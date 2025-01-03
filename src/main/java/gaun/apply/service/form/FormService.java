package gaun.apply.service.form;

import java.time.LocalDate;

import gaun.apply.dto.form.WirelessNetworkFormDto;
import gaun.apply.entity.form.WirelessNetworkFormData;
import gaun.apply.repository.form.IpMacFormRepository;
import org.springframework.stereotype.Service;
import gaun.apply.entity.form.BaseFormData;
import gaun.apply.repository.form.WirelessNetworkFormRepository;
// ... diğer importlar

@Service
public class FormService {
    private final WirelessNetworkFormRepository wirelessNetworkFormRepository;
    private final IpMacFormRepository ipMacFormRepository;
    // ... diğer repository'ler

    public FormService(WirelessNetworkFormRepository wirelessNetworkFormRepository,
                      IpMacFormRepository ipMacFormRepository /* diğer repository'ler */) {
        this.wirelessNetworkFormRepository = wirelessNetworkFormRepository;
        this.ipMacFormRepository = ipMacFormRepository;
        // ... diğer atamalar
    }

    public void saveWirelessNetworkForm(WirelessNetworkFormDto dto, String username) {
        WirelessNetworkFormData form = new WirelessNetworkFormData();
        form.setUsername(username);
        form.setApplyDate(LocalDate.now());
        form.setStatus(false);
        form.setMacAddress(dto.getMacAddress());
        form.setDeviceType(dto.getDeviceType());
        wirelessNetworkFormRepository.save(form);
    }

    // Diğer form kaydetme metodları...
} 