-- Remote Config Tables Migration
-- Version: 1.0
-- Description: Creates tables for mobile app remote configuration

-- Create useful_links table
CREATE TABLE IF NOT EXISTS useful_links (
    id VARCHAR(50) PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    icon VARCHAR(100) NOT NULL,
    url TEXT NOT NULL,
    description TEXT,
    color VARCHAR(7) NOT NULL,
    order_index INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_is_active (is_active),
    INDEX idx_order_index (order_index)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create app_config table
CREATE TABLE IF NOT EXISTS app_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    config_key VARCHAR(100) UNIQUE NOT NULL,
    config_value TEXT NOT NULL,
    version VARCHAR(20) NOT NULL,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_config_key (config_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert initial useful links data
INSERT INTO useful_links (id, title, icon, url, description, color, order_index, is_active) VALUES
('1', 'Hastane Randevu', 'medical-outline', 'https://ganteptiphastaportali.mergentech.com.tr/#/auth/login', 'GAÜN Tıp Fakültesi Hastanesi randevu sistemi', '#CC0000', 1, TRUE),
('2', 'Eduroam/ E-posta', 'document-text-outline', 'https://hesap.gaziantep.edu.tr/', 'Öğrenci ve personel eduroam ve e-posta başvuru işlemleri', '#003366', 2, TRUE),
('3', 'Telefon Rehberi', 'people-outline', 'https://rehber.gaziantep.edu.tr/', 'GAÜN personel ve birim telefon rehberi', '#0099CC', 3, TRUE),
('4', 'GAÜN Sporium', 'fitness-outline', 'https://sporium.gaziantep.edu.tr/', 'GAÜN sporium merkezi', '#003366', 4, TRUE),
('5', 'Akademik Takvim', 'calendar-outline', 'https://oidb.gaziantep.edu.tr/page.php?url=akademik-takvim-4', 'Akademik takvim', '#CC0000', 5, TRUE);

-- Insert initial config version
INSERT INTO app_config (config_key, config_value, version) VALUES
('useful_links_version', 'Initial configuration for useful links', '1.0.0');

-- Create audit log table (optional, for tracking changes)
CREATE TABLE IF NOT EXISTS config_audit_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    entity_type VARCHAR(50) NOT NULL,
    entity_id VARCHAR(50) NOT NULL,
    action VARCHAR(20) NOT NULL,
    old_value TEXT,
    new_value TEXT,
    changed_by VARCHAR(100),
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_entity (entity_type, entity_id),
    INDEX idx_changed_at (changed_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
