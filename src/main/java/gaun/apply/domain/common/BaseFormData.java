package gaun.apply.domain.common;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import gaun.apply.common.enums.ApplicationStatusEnum;

@Data
@MappedSuperclass
public abstract class BaseFormData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "tc_kimlik_no")
    private String tcKimlikNo;
    @Column(name = "apply_date")
    private LocalDateTime applyDate;
    @Column(name = "is_active")
    private Boolean status = false;
    @Column(name = "approval_date")
    private LocalDateTime approvalDate;
    @Column(name = "rejected")
    private boolean rejected;
    @Column(name = "rejection_reason")
    private String rejectionReason;
    @Column(name = "rejection_date")
    private LocalDateTime rejectionDate;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Enumerated(EnumType.STRING)
    @Column(name = "application_status")
    private ApplicationStatusEnum applicationStatus = ApplicationStatusEnum.PENDING;

    public boolean isStatus() {
        return status != null && status;
    }
}
