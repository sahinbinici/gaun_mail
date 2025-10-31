package gaun.apply.domain.user.service;

import gaun.apply.config.db.DbAkademikConfig;
import gaun.apply.config.db.DbIdariConfig;
import gaun.apply.config.db.DbSurekliConfig;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import gaun.apply.application.dto.StaffDto;
import gaun.apply.domain.user.entity.Staff;
import gaun.apply.domain.user.repository.StaffRepository;

import java.sql.Date;

@Service
public class StaffService {
    private final StaffRepository staffRepository;
    private final ModelMapper modelMapper;
    private final StaffRepository idariRepo;
    private final StaffRepository akademikRepo;
    private final StaffRepository surekliRepo;

    public StaffService(StaffRepository staffRepository, @Qualifier("personelIdariRepository")StaffRepository idariRepo, @Qualifier("personelAkademikRepository")StaffRepository akademikRepo, @Qualifier("personelSurekliRepository")StaffRepository surekliRepo) {
        this.staffRepository = staffRepository;
        this.idariRepo = idariRepo;
        this.akademikRepo = akademikRepo;
        this.surekliRepo = surekliRepo;
        this.modelMapper = new ModelMapper();

        modelMapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE);
    }

    public Staff findByTcKimlikNo(String tcKimlikNo) {
        return staffRepository.findByTcKimlikNo(tcKimlikNo);
    }

    public StaffDto findStaffDtoByTcKimlikNo(String tcKimlikNo) {
        StaffDto staffDto = getStaffDto(tcKimlikNo);

        return staffDto;
    }

    private StaffDto getStaffDto(String tcKimlikNo) {

        //Object[] result = (Object[]) staffRepository.findStaffByTcKimlikNo(tcKimlikNo);
        Object[] result = null;
         if(idariRepo.findStaffByTcKimlikNo(tcKimlikNo)!=null){
             result = (Object[]) idariRepo.findStaffByTcKimlikNo(tcKimlikNo);
         } else if (akademikRepo.findStaffByTcKimlikNo(tcKimlikNo)!=null) {
             result = (Object[]) akademikRepo.findStaffByTcKimlikNo(tcKimlikNo);
         }else if (surekliRepo.findStaffByTcKimlikNo(tcKimlikNo)!=null) {
             result = (Object[]) surekliRepo.findStaffByTcKimlikNo(tcKimlikNo);
         }

        if (result == null) {
            return null;
        }

        StaffDto staffDto = new StaffDto();
        staffDto.setTcKimlikNo((Long) result[0]);
        staffDto.setSicilNo((Integer) result[1]);
        staffDto.setAd((String) result[2]);
        staffDto.setSoyad((String) result[3]);
        staffDto.setCalistigiBirim((String) result[4]);
        staffDto.setUnvan((String) result[5]);
        staffDto.setGsm((String) result[6]);
        staffDto.setDogumTarihi((Date) result[7]);
        return staffDto;
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
        return (adIlkHarf+staff.getSoyad().toLowerCase()).replace("ı","i").replace("ö","o").replace("ü","u").replace("ğ","g").replace("ş","s").toLowerCase();
    }
}