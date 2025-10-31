-- ============================================
-- Remote Config Database Setup Script
-- ============================================
-- Description: Creates tables and initial data for mobile app remote configuration
-- Version: 1.0
-- Date: 2025-10-14
-- ============================================

-- Use your database
-- USE your_database_name;

-- ============================================
-- 1. CREATE TABLES
-- ============================================

-- Drop tables if they exist (for clean setup)
-- WARNING: This will delete all data!
-- DROP TABLE IF EXISTS config_audit_log;
-- DROP TABLE IF EXISTS useful_links;
-- DROP TABLE IF EXISTS app_config;

-- Create useful_links table
CREATE TABLE IF NOT EXISTS useful_links (
    id VARCHAR(50) PRIMARY KEY COMMENT 'Unique identifier for the link',
    title VARCHAR(200) NOT NULL COMMENT 'Display title of the link',
    icon VARCHAR(100) NOT NULL COMMENT 'Ionicons icon name',
    url TEXT NOT NULL COMMENT 'Target URL',
    description TEXT COMMENT 'Description of the link',
    color VARCHAR(7) NOT NULL COMMENT 'Hex color code (e.g., #CC0000)',
    order_index INT DEFAULT 0 COMMENT 'Display order (lower = first)',
    is_active BOOLEAN DEFAULT TRUE COMMENT 'Whether the link is active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation timestamp',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Last update timestamp',
    INDEX idx_is_active (is_active),
    INDEX idx_order_index (order_index)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Stores useful links for mobile app';

-- Create app_config table
CREATE TABLE IF NOT EXISTS app_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'Auto-increment ID',
    config_key VARCHAR(100) UNIQUE NOT NULL COMMENT 'Configuration key',
    config_value TEXT NOT NULL COMMENT 'Configuration value',
    version VARCHAR(20) NOT NULL COMMENT 'Version number',
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Last update timestamp',
    INDEX idx_config_key (config_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Stores app configuration and versioning';

-- Create audit log table (optional, for tracking changes)
CREATE TABLE IF NOT EXISTS config_audit_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'Auto-increment ID',
    entity_type VARCHAR(50) NOT NULL COMMENT 'Type of entity (e.g., useful_link)',
    entity_id VARCHAR(50) NOT NULL COMMENT 'ID of the entity',
    action VARCHAR(20) NOT NULL COMMENT 'Action performed (CREATE, UPDATE, DELETE)',
    old_value TEXT COMMENT 'Old value (JSON)',
    new_value TEXT COMMENT 'New value (JSON)',
    changed_by VARCHAR(100) COMMENT 'User who made the change',
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Change timestamp',
    INDEX idx_entity (entity_type, entity_id),
    INDEX idx_changed_at (changed_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Audit log for configuration changes';

-- ============================================
-- 2. INSERT INITIAL DATA
-- ============================================

-- Insert initial useful links
INSERT INTO useful_links (id, title, icon, url, description, color, order_index, is_active) VALUES
('1', 'Hastane Randevu', 'medical-outline', 'https://ganteptiphastaportali.mergentech.com.tr/#/auth/login', 'GAÜN Tıp Fakültesi Hastanesi randevu sistemi', '#CC0000', 1, TRUE),
('2', 'Eduroam/ E-posta', 'document-text-outline', 'https://hesap.gaziantep.edu.tr/', 'Öğrenci ve personel eduroam ve e-posta başvuru işlemleri', '#003366', 2, TRUE),
('3', 'Telefon Rehberi', 'people-outline', 'https://rehber.gaziantep.edu.tr/', 'GAÜN personel ve birim telefon rehberi', '#0099CC', 3, TRUE),
('4', 'GAÜN Sporium', 'fitness-outline', 'https://sporium.gaziantep.edu.tr/', 'GAÜN sporium merkezi', '#003366', 4, TRUE),
('5', 'Akademik Takvim', 'calendar-outline', 'https://oidb.gaziantep.edu.tr/page.php?url=akademik-takvim-4', 'Akademik takvim', '#CC0000', 5, TRUE),
('6', 'Gimer', 'document-text-outline', 'https://kys.gantep.edu.tr/', 'GAÜN Kalite yönetim bilgi sistemi', '#003366', 2, TRUE);

-- Insert initial config version
INSERT INTO app_config (config_key, config_value, version) VALUES
('useful_links_version', 'Initial configuration for useful links', '1.0.0');

-- ============================================
-- 3. VERIFICATION QUERIES
-- ============================================

-- Verify useful links
SELECT 'Useful Links Count:' as info, COUNT(*) as count FROM useful_links;
SELECT * FROM useful_links ORDER BY order_index;

-- Verify config
SELECT 'App Config Count:' as info, COUNT(*) as count FROM app_config;
SELECT * FROM app_config;

-- ============================================
-- 4. USEFUL QUERIES FOR MANAGEMENT
-- ============================================

-- Add a new link
-- INSERT INTO useful_links (id, title, icon, url, description, color, order_index, is_active)
-- VALUES ('6', 'Kütüphane', 'book-outline', 'https://kutuphane.gaziantep.edu.tr/', 'GAÜN Kütüphane sistemi', '#009900', 6, TRUE);

-- Update a link
-- UPDATE useful_links
-- SET title = 'Yeni Başlık', url = 'https://yeni-url.com'
-- WHERE id = '1';

-- Deactivate a link (soft delete)
-- UPDATE useful_links
-- SET is_active = FALSE
-- WHERE id = '5';

-- Reactivate a link
-- UPDATE useful_links
-- SET is_active = TRUE
-- WHERE id = '5';

-- Change link order
-- UPDATE useful_links
-- SET order_index = 10
-- WHERE id = '3';

-- Delete a link permanently
-- DELETE FROM useful_links WHERE id = '6';

-- Update config version (done automatically by service, but can be manual)
-- UPDATE app_config
-- SET version = '1.0.1'
-- WHERE config_key = 'useful_links_version';

-- View active links only
-- SELECT * FROM useful_links WHERE is_active = TRUE ORDER BY order_index;

-- View all links with status
-- SELECT
--     id,
--     title,
--     CASE WHEN is_active THEN 'Active' ELSE 'Inactive' END as status,
--     order_index,
--     url
-- FROM useful_links
-- ORDER BY order_index;

-- ============================================
-- 5. MAINTENANCE QUERIES
-- ============================================

-- Reorder all links sequentially
-- SET @row_number = 0;
-- UPDATE useful_links
-- SET order_index = (@row_number:=@row_number + 1)
-- ORDER BY order_index;

-- Find duplicate IDs (should return empty)
-- SELECT id, COUNT(*) as count
-- FROM useful_links
-- GROUP BY id
-- HAVING count > 1;

-- Find links with invalid colors (should return empty)
-- SELECT id, title, color
-- FROM useful_links
-- WHERE color NOT REGEXP '^#[0-9A-Fa-f]{6}$';

-- ============================================
-- 6. BACKUP QUERIES
-- ============================================

-- Backup useful links to a temporary table
-- CREATE TABLE useful_links_backup AS SELECT * FROM useful_links;

-- Restore from backup
-- TRUNCATE TABLE useful_links;
-- INSERT INTO useful_links SELECT * FROM useful_links_backup;

-- ============================================
-- SETUP COMPLETE
-- ============================================

SELECT '✓ Remote Config tables created successfully!' as status;
SELECT '✓ Initial data inserted!' as status;
SELECT '✓ You can now start the Spring Boot application!' as status;
