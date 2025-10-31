package gaun.apply.domain.user.service;

import gaun.apply.application.dto.StaffDto;
import gaun.apply.domain.user.entity.Staff;
import gaun.apply.domain.user.repository.CalyerKurumdisiRepository;
import gaun.apply.domain.user.repository.StaffRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.sql.Date;

@Service
public class StaffService {
    private static final Logger logger = LoggerFactory.getLogger(StaffService.class);

    private final StaffRepository staffRepository;
    private final ModelMapper modelMapper;
    private final StaffRepository idariRepo;
    private final StaffRepository akademikRepo;
    private final StaffRepository surekliRepo;
    private final CalyerKurumdisiRepository calyerKurumdisiRepo;

    public StaffService(StaffRepository staffRepository,
                       @Qualifier("personelIdariRepository")StaffRepository idariRepo,
                       @Qualifier("personelAkademikRepository")StaffRepository akademikRepo,
                       @Qualifier("personelSurekliRepository")StaffRepository surekliRepo,
                       @Qualifier("calyerKurumdisiAkademikRepository")CalyerKurumdisiRepository calyerKurumdisiRepo) {
        this.staffRepository = staffRepository;
        this.idariRepo = idariRepo;
        this.akademikRepo = akademikRepo;
        this.surekliRepo = surekliRepo;
        this.calyerKurumdisiRepo = calyerKurumdisiRepo;
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
        logger.debug("TC Kimlik No ile personel aranıyor: {}", tcKimlikNo);

        Object[] result = null;

        // İdari personel kontrolü
        result = (Object[]) idariRepo.findStaffByTcKimlikNo(tcKimlikNo);
        if(result != null){
            logger.debug("Personel idari tablosunda bulundu: {}", tcKimlikNo);
            return mapToStaffDto(result);
        }

        // Akademik personel kontrolü
        result = (Object[]) akademikRepo.findStaffByTcKimlikNo(tcKimlikNo);
        if (result != null) {
            logger.debug("Personel akademik tablosunda bulundu: {}", tcKimlikNo);
            return mapToStaffDto(result);
        }

        // Sürekli personel kontrolü
        result = (Object[]) surekliRepo.findStaffByTcKimlikNo(tcKimlikNo);
        if (result != null) {
            logger.debug("Personel sürekli tablosunda bulundu: {}", tcKimlikNo);
            return mapToStaffDto(result);
        }

        // Çalışma yeri kurum dışı kontrolü
        logger.debug("Personel person tablolarında bulunamadı, calyerkurumdisi tablosuna bakılıyor: {}", tcKimlikNo);
        result = (Object[]) calyerKurumdisiRepo.findStaffByTcKimlikNo(tcKimlikNo);
        if (result != null) {
            logger.debug("Personel calyerkurumdisi tablosunda bulundu: {}", tcKimlikNo);
            return mapToStaffDto(result);
        }

        logger.warn("Personel hiçbir tabloda bulunamadı: {}", tcKimlikNo);
        return null;
    }

    private StaffDto mapToStaffDto(Object[] result) {
        StaffDto staffDto = new StaffDto();
        staffDto.setTcKimlikNo((Long) result[0]);
        staffDto.setSicilNo(result[1] != null ? (Integer) result[1] : null);
        staffDto.setAd((String) result[2]);
        staffDto.setSoyad((String) result[3]);
        staffDto.setCalistigiBirim(result[4] != null ? (String) result[4] : null);
        staffDto.setUnvan(result[5] != null ? (String) result[5] : null);
        staffDto.setGsm(result[6] != null ? (String) result[6] : null);
        staffDto.setDogumTarihi(result[7] != null ? (Date) result[7] : null);
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
