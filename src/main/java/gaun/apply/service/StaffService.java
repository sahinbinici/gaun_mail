package gaun.apply.service;

import gaun.apply.entity.Staff;

public interface StaffService {
    Staff findByTcKimlikNo(String tcKimlikNo);
}
