package gaun.apply.service;

import gaun.apply.entity.AdminTabPermission;
import gaun.apply.repository.AdminTabPermissionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminTabPermissionService {
    private final AdminTabPermissionRepository adminTabPermissionRepository;

    public AdminTabPermissionService(AdminTabPermissionRepository adminTabPermissionRepository) {
        this.adminTabPermissionRepository = adminTabPermissionRepository;
    }

    public Map<String, Boolean> getTabPermissions(Long userId) {
        List<AdminTabPermission> permissions = adminTabPermissionRepository.findByUserId(userId);
        return permissions.stream()
                .collect(Collectors.toMap(
                    AdminTabPermission::getTabName,
                    AdminTabPermission::isHasAccess
                ));
    }

    public boolean hasTabPermission(Long userId, String tabName) {
        return adminTabPermissionRepository.existsByUserIdAndTabNameAndHasAccessTrue(userId, tabName);
    }

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