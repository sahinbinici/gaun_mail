package gaun.apply.service.impl;

import org.springframework.stereotype.Service;

import gaun.apply.entity.Staff;
import gaun.apply.repository.StaffRepository;
import gaun.apply.service.StaffService;

@Service
public class StaffServiceImpl implements StaffService {
    private final StaffRepository staffRepository;

    public StaffServiceImpl(StaffRepository staffRepository) {
        this.staffRepository = staffRepository;
    }

    @Override
    public Staff findByTcKimlikNo(String tcKimlikNo) {
        return staffRepository.findByTcKimlikNo(tcKimlikNo);
    }
} 