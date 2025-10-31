
package gaun.apply.application.dto;

import gaun.apply.domain.eduroam.entity.EduroamFormData;
import gaun.apply.domain.mail.entity.MailFormData;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Dashboard data DTO
 */
@Getter
@Setter
public class DashboardDataDTO {
    // Getters and setters
    private List<MailFormData> mailForms;
    private List<EduroamFormData> eduroamForms;
    private Map<String, Long> mailStats;
    private Map<String, Long> eduroamStats;
    private Map<String, Long> userStats;

    public DashboardDataDTO() {
        this.mailForms = new ArrayList<>();
        this.eduroamForms = new ArrayList<>();
        this.mailStats = new HashMap<>();
        this.eduroamStats = new HashMap<>();
        this.userStats = new HashMap<>();
    }

}