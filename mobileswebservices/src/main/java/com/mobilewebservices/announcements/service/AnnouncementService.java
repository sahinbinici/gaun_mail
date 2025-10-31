package com.mobilewebservices.announcements.service;

import com.mobilewebservices.announcements.dto.AnnouncementDto;
import com.mobilewebservices.announcements.repository.AnnouncementRepository;
import com.mobilewebservices.common.dto.PageRequest;
import com.mobilewebservices.common.dto.PageResponse;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;

    public AnnouncementService(AnnouncementRepository announcementRepository) {
        this.announcementRepository = announcementRepository;
    }

    @Cacheable(value = "announcements", key = "'all'")
    public List<AnnouncementDto> getAllAnnouncements() {
        return announcementRepository.findAll();
    }

    @Cacheable(value = "announcements", key = "#id")
    public AnnouncementDto getAnnouncementById(Long id) {
        return announcementRepository.findById(id);
    }

    @Cacheable(value = "announcements", key = "'section-' + #sectionId")
    public List<AnnouncementDto> getAnnouncementsBySectionId(Long sectionId) {
        return announcementRepository.findBySectionId(sectionId);
    }

    @Cacheable(value = "announcements", key = "'author-' + #authorId")
    public List<AnnouncementDto> getAnnouncementsByAuthorId(Long authorId) {
        return announcementRepository.findByAuthorId(authorId);
    }

    @Cacheable(value = "latest-announcements", key = "'latest-10'")
    public List<AnnouncementDto> getLatest10Announcements() {
        return announcementRepository.findLatest10Announcements();
    }

    @Cacheable(value = "announcements", key = "'page-' + #pageRequest.page + '-size-' + #pageRequest.size + '-sort-' + #pageRequest.sortBy + '-' + #pageRequest.sortDirection")
    public PageResponse<AnnouncementDto> getAllAnnouncementsPaginated(PageRequest pageRequest) {
        List<AnnouncementDto> announcements = announcementRepository.findAllWithPagination(pageRequest);
        long totalElements = announcementRepository.countAll();
        return PageResponse.of(announcements, pageRequest, totalElements);
    }

    @Cacheable(value = "announcements", key = "'section-' + #sectionId + '-page-' + #pageRequest.page + '-size-' + #pageRequest.size")
    public PageResponse<AnnouncementDto> getAnnouncementsBySectionIdPaginated(Long sectionId, PageRequest pageRequest) {
        List<AnnouncementDto> announcements = announcementRepository.findBySectionIdWithPagination(sectionId, pageRequest);
        long totalElements = announcementRepository.countBySectionId(sectionId);
        return PageResponse.of(announcements, pageRequest, totalElements);
    }

    @Cacheable(value = "announcements", key = "'last-month'")
    public List<AnnouncementDto> getAnnouncementsFromLastMonth() {
        return announcementRepository.findAnnouncementsFromLastMonth();
    }
}
