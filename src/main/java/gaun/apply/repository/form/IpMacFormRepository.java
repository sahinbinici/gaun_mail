package gaun.apply.repository.form;

import java.util.List;

import org.springframework.stereotype.Repository;
import gaun.apply.entity.form.IpMacFormData;

@Repository
public interface IpMacFormRepository extends BaseFormRepository<IpMacFormData> {
    IpMacFormData findByTcKimlikNo(String tcKimlikNo);
    long countByStatus(boolean status);
    List<IpMacFormData> findTop10ByOrderByApplyDateDesc();
} 