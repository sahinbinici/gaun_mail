package gaun.apply.service;

import gaun.apply.entity.Student;
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

    public Staff findByTcKimlikNo(String tcKimlikNo) {
        return staffRepository.findByTcKimlikNo(tcKimlikNo);
    }

    public StaffDto findByStaffTCKimlikNo(String tcKimlikNo) {
        return modelMapper.map(staffRepository.findByTcKimlikNo(tcKimlikNo), StaffDto.class);
    }

    public void saveStaff(StaffDto staffDto) {
        Staff staff=modelMapper.map(staffDto, Staff.class);
        staffRepository.save(staff);
    }

    public Staff save(Staff staff) {
        return staffRepository.save(staff);
    }

    public String createEmailAddress(String tcKimlikNo) {
        Staff staff=staffRepository.findByTcKimlikNo(tcKimlikNo);
        String adIlkHarf=staff.getAd().substring(0,1);
        return (adIlkHarf+staff.getSoyad().toLowerCase()).replace("ı","i").replace("ö","o").replace("ü","u").replace("ğ","g");
    }
} 