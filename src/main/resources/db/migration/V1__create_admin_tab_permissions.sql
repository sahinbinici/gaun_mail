CREATE TABLE admin_tab_permissions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    tab_name VARCHAR(50) NOT NULL,
    has_access BOOLEAN NOT NULL DEFAULT FALSE,
    UNIQUE KEY uk_user_tab (user_id, tab_name)
); 