package com.mobilewebservices.config.service;

import com.mobilewebservices.config.dto.ConfigVersionDto;
import com.mobilewebservices.config.dto.UsefulLinkDto;
import com.mobilewebservices.config.repository.AppConfigRepository;
import com.mobilewebservices.config.repository.UsefulLinkRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class RemoteConfigService {

    private final UsefulLinkRepository usefulLinkRepository;
    private final AppConfigRepository appConfigRepository;

    private static final String CONFIG_VERSION_KEY = "useful_links_version";
    private static final String MIN_APP_VERSION = "1.0.0";

    /**
     * Get all active useful links
     * Cached for 1 hour
     */
    @Cacheable(value = "usefulLinks", unless = "#result == null || #result.isEmpty()")
    public List<UsefulLinkDto> getActiveUsefulLinks() {
        log.info("Fetching active useful links from database");
        return usefulLinkRepository.findByIsActiveTrueOrderByOrderIndexAsc();
    }

    /**
     * Get all useful links (including inactive)
     */
    public List<UsefulLinkDto> getAllUsefulLinks() {
        log.info("Fetching all useful links from database");
        return usefulLinkRepository.findAllByOrderByOrderIndexAsc();
    }

    /**
     * Get configuration version
     */
    @Cacheable(value = "configVersion", unless = "#result == null")
    public ConfigVersionDto getConfigVersion() {
        log.info("Fetching config version");

        Map<String, Object> config = appConfigRepository.findByConfigKey(CONFIG_VERSION_KEY)
                .orElseGet(() -> {
                    createDefaultConfigVersion();
                    return appConfigRepository.findByConfigKey(CONFIG_VERSION_KEY).orElse(null);
                });

        if (config == null) {
            return ConfigVersionDto.builder()
                    .version("1.0.0")
                    .lastUpdated(LocalDateTime.now().toString())
                    .minAppVersion(MIN_APP_VERSION)
                    .build();
        }

        return ConfigVersionDto.builder()
                .version((String) config.get("version"))
                .lastUpdated(config.get("last_updated").toString())
                .minAppVersion(MIN_APP_VERSION)
                .build();
    }

    /**
     * Create a new useful link
     */
    @CacheEvict(value = "usefulLinks", allEntries = true)
    public UsefulLinkDto createUsefulLink(UsefulLinkDto dto) {
        log.info("Creating new useful link: {}", dto.getTitle());

        if (usefulLinkRepository.existsById(dto.getId())) {
            throw new IllegalArgumentException("Link with ID " + dto.getId() + " already exists");
        }

        usefulLinkRepository.save(dto);
        incrementConfigVersion();

        return dto;
    }

    /**
     * Update an existing useful link
     */
    @CacheEvict(value = "usefulLinks", allEntries = true)
    public UsefulLinkDto updateUsefulLink(String id, UsefulLinkDto dto) {
        log.info("Updating useful link: {}", id);

        if (!usefulLinkRepository.existsById(id)) {
            throw new IllegalArgumentException("Link with ID " + id + " not found");
        }

        usefulLinkRepository.update(id, dto);
        incrementConfigVersion();

        return dto;
    }

    /**
     * Delete a useful link
     */
    @CacheEvict(value = "usefulLinks", allEntries = true)
    public void deleteUsefulLink(String id) {
        log.info("Deleting useful link: {}", id);

        if (!usefulLinkRepository.existsById(id)) {
            throw new IllegalArgumentException("Link with ID " + id + " not found");
        }

        usefulLinkRepository.deleteById(id);
        incrementConfigVersion();
    }

    /**
     * Toggle link active status
     */
    @CacheEvict(value = "usefulLinks", allEntries = true)
    public UsefulLinkDto toggleLinkStatus(String id) {
        log.info("Toggling status for useful link: {}", id);

        if (!usefulLinkRepository.existsById(id)) {
            throw new IllegalArgumentException("Link with ID " + id + " not found");
        }

        usefulLinkRepository.toggleStatus(id);
        incrementConfigVersion();

        return usefulLinkRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Link with ID " + id + " not found"));
    }

    /**
     * Clear all caches
     */
    @CacheEvict(value = {"usefulLinks", "configVersion"}, allEntries = true)
    public void clearAllCaches() {
        log.info("Clearing all remote config caches");
    }

    // Helper methods

    private void incrementConfigVersion() {
        Map<String, Object> config = appConfigRepository.findByConfigKey(CONFIG_VERSION_KEY).orElse(null);

        String currentVersion;
        if (config == null) {
            createDefaultConfigVersion();
            currentVersion = "1.0.0";
        } else {
            currentVersion = (String) config.get("version");
        }

        // Increment version (e.g., 1.0.0 -> 1.0.1)
        String newVersion = incrementVersion(currentVersion);
        appConfigRepository.updateVersion(CONFIG_VERSION_KEY, newVersion);

        // Clear version cache
        clearVersionCache();

        log.info("Config version incremented from {} to {}", currentVersion, newVersion);
    }

    @CacheEvict(value = "configVersion", allEntries = true)
    private void clearVersionCache() {
        // Cache eviction handled by annotation
    }

    private String incrementVersion(String version) {
        String[] parts = version.split("\\.");
        if (parts.length == 3) {
            int patch = Integer.parseInt(parts[2]);
            return parts[0] + "." + parts[1] + "." + (patch + 1);
        }
        return "1.0.1";
    }

    private void createDefaultConfigVersion() {
        appConfigRepository.save(CONFIG_VERSION_KEY, "Initial version", "1.0.0");
    }
}
