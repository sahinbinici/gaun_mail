package gaun.apply.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "admin_tab_permissions")
public class AdminTabPermission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "tab_name")
    private String tabName; // mail, eduroam, vpn, ip-mac, cloud

    @Column(name = "has_access")
    private boolean hasAccess;
} 