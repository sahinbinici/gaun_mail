package gaun.apply.infrastructure.service;

import gaun.apply.infrastructure.entity.AdminTabPermission;
import gaun.apply.infrastructure.repository.AdminTabPermissionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for managing admin tab permissions
 */
@Service
public class AdminTabPermissionService {
    private final AdminTabPermissionRepository adminTabPermissionRepository;

    public AdminTabPermissionService(AdminTabPermissionRepository adminTabPermissionRepository) {
        this.adminTabPermissionRepository = adminTabPermissionRepository;
    }

    /**
     * Get tab permissions for a user
     * 
     * @param userId The user's ID
     * @return Map of tab names to permission status
     */
    public Map<String, Boolean> getTabPermissions(Long userId) {
        List<AdminTabPermission> permissions = adminTabPermissionRepository.findByUserId(userId);
        return permissions.stream()
                .collect(Collectors.toMap(
                    AdminTabPermission::getTabName,
                    AdminTabPermission::isHasAccess
                ));
    }

    /**
     * Check if a user has permission to a specific tab
     * 
     * @param userId The user's ID
     * @param tabName The tab name to check permission for
     * @return true if the user has permission, false otherwise
     */
    public boolean hasTabPermission(Long userId, String tabName) {
        return adminTabPermissionRepository.existsByUserIdAndTabNameAndHasAccessTrue(userId, tabName);
    }

    /**
     * Set permission for a user to access a specific tab
     * 
     * @param userId The user's ID
     * @param tabName The tab name
     * @param hasAccess Whether the user should have permission
     */
    public void setTabPermission(Long userId, String tabName, boolean hasAccess) {
        AdminTabPermission permission = adminTabPermissionRepository.findByUserId(userId)
                .stream()
                .filter(p -> p.getTabName().equals(tabName))
                .findFirst()
                .orElse(new AdminTabPermission());

        permission.setUserId(userId);
        permission.setTabName(tabName);
        permission.setHasAccess(hasAccess);

        adminTabPermissionRepository.save(permission);
    }
}
