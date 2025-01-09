package gaun.apply.service;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import gaun.apply.dto.StaffDto;
import gaun.apply.entity.Staff;
import gaun.apply.repository.StaffRepository;

@Service
public class StaffService {
    private final StaffRepository staffRepository;
    private final ModelMapper modelMapper;

    public StaffService(StaffRepository staffRepository) {
        this.staffRepository = staffRepository;
        this.modelMapper = new ModelMapper();

        modelMapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE);
    }

    public Staff findByTcKimlikNo(String tcKimlikNo)
    {
        System.out.println("StaffService.findByTcKimlikNo"+staffRepository.findByTcKimlikNo(tcKimlikNo));
        return staffRepository.findByTcKimlikNo(tcKimlikNo);
    }

    public void saveStaff(StaffDto staffDto) {
        Staff staff=modelMapper.map(staffDto, Staff.class);
        staffRepository.save(staff);
    }

    public Staff save(Staff staff) {
        return staffRepository.save(staff);
    }
} 