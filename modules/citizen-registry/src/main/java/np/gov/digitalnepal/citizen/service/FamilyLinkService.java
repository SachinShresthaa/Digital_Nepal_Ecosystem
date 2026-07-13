package np.gov.digitalnepal.citizen.service;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import np.gov.digitalnepal.citizen.dto.FamilyLinkDto;
import np.gov.digitalnepal.citizen.dto.FamilyTreeResponse;
import np.gov.digitalnepal.citizen.entity.Citizen;
import np.gov.digitalnepal.citizen.entity.FamilyLink;
import np.gov.digitalnepal.citizen.enums.LinkStatus;
import np.gov.digitalnepal.citizen.enums.RelationType;
import np.gov.digitalnepal.citizen.repository.CitizenRepository;
import np.gov.digitalnepal.citizen.repository.FamilyLinkRepository;
import np.gov.digitalnepal.platformaudit.audit.AuditEventType;
import np.gov.digitalnepal.platformaudit.audit.AuditLogService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FamilyLinkService {
    private final FamilyLinkRepository familyLinkRepository;
    private final CitizenRepository citizenRepository;
    private final AuditLogService auditLogService;

    // CREATE FAMILY LINKS — called during registration
    @Transactional
    public void createFamilyLinks(Citizen citizen, Map<RelationType, String> familyMemberNos) {
        if (familyMemberNos == null || familyMemberNos.isEmpty()) return;

        for (Map.Entry<RelationType, String> entry : familyMemberNos.entrySet()) {
            RelationType relationType = entry.getKey();
            String relatedCitizenshipNoNorm = entry.getValue();

            if (relatedCitizenshipNoNorm == null || relatedCitizenshipNoNorm.isBlank()) continue;

            // Prevent duplicate links
            if (familyLinkRepository.existsByCitizenIdAndRelatedCitizenshipNo(
                    citizen.getId(), relatedCitizenshipNoNorm)) {
                log.debug("Family link already exists — skipping: {} → {}",
                        citizen.getId(), relatedCitizenshipNoNorm);
                continue;
            }

            // Check if the related citizen is already registered
            Optional<Citizen> relatedCitizen = citizenRepository
                    .findByCitizenshipNoNormAndIsActiveTrue(relatedCitizenshipNoNorm);

            FamilyLink link;

            if (relatedCitizen.isPresent()) {
                // Related citizen EXISTS — create LINKED immediately
                link = FamilyLink.builder()
                        .citizen(citizen)
                        .relationType(relationType)
                        .relatedCitizen(relatedCitizen.get())
                        .relatedNameText(relatedCitizen.get().getNameNp())
                        .relatedCitizenshipNo(relatedCitizenshipNoNorm)
                        .linkStatus(LinkStatus.LINKED)
                        .build();

                log.info("Family link LINKED — citizen: {} → {} ({})",
                        citizen.getId(), relatedCitizen.get().getId(), relationType);

            } else {
                // Related citizen NOT registered yet — create PENDING link
                // Will be resolved when they register (resolvePendingLinks)
                link = FamilyLink.builder()
                        .citizen(citizen)
                        .relationType(relationType)
                        .relatedCitizen(null)
                        .relatedCitizenshipNo(relatedCitizenshipNoNorm)
                        .linkStatus(LinkStatus.PENDING)
                        .build();

                log.info("Family link PENDING — citizen: {} waiting for citizenshipNo: {} ({})",
                        citizen.getId(), relatedCitizenshipNoNorm, relationType);
            }

            familyLinkRepository.save(link);
        }

        auditLogService.log(
                AuditEventType.CITIZEN_UPDATED,
                citizen.getId(),
                "Family links created during registration"
        );
    }

    // RESOLVE PENDING LINKS — called when a new citizen registers
    @Transactional
    public void resolvePendingLinks(Citizen newCitizen) {
        List<FamilyLink> pendingLinks = familyLinkRepository.findPendingByRelatedCitizenshipNo(
                newCitizen.getCitizenshipNoNorm(),
                LinkStatus.PENDING
        );

        if (pendingLinks.isEmpty()) return;

        for (FamilyLink pendingLink : pendingLinks) {
            pendingLink.setRelatedCitizen(newCitizen);
            pendingLink.setRelatedNameText(newCitizen.getNameNp());
            pendingLink.setLinkStatus(LinkStatus.LINKED);
            familyLinkRepository.save(pendingLink);

            log.info("Pending family link resolved — linkId: {}, relatedCitizen: {}",
                    pendingLink.getId(), newCitizen.getId());
        }

        log.info("Resolved {} pending family links for citizen: {}",
                pendingLinks.size(), newCitizen.getId());
    }

    // GET FAMILY TREE — used by GET /api/v1/citizens/{id}/family
    @Transactional(readOnly = true)
    public FamilyTreeResponse getFamilyTree(UUID citizenId) {
        Citizen citizen = citizenRepository.findById(citizenId)
                .orElseThrow(() -> new RuntimeException("Citizen not found: " + citizenId));

        List<FamilyLink> links = familyLinkRepository.findByCitizenId(citizenId);

        List<FamilyLinkDto> linkDtos = links.stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        long pendingCount = linkDtos.stream()
                .filter(dto -> dto.getLinkStatus() == LinkStatus.PENDING)
                .count();

        return FamilyTreeResponse.builder()
                .citizenId(citizen.getId())
                .nameNp(citizen.getNameNp())
                .nameEn(citizen.getNameEn())
                .familyLinks(linkDtos)
                .totalLinks(linkDtos.size())
                .pendingLinks(pendingCount)
                .build();
    }

    // PRIVATE HELPERS
    private FamilyLinkDto toDto(FamilyLink link) {
        return FamilyLinkDto.builder()
                .linkId(link.getId())
                .relationType(link.getRelationType())
                .relatedCitizenId(link.getRelatedCitizen() != null
                        ? link.getRelatedCitizen().getId() : null)
                .relatedNameText(link.getRelatedNameText())
                .linkStatus(link.getLinkStatus())
                .build();
    }
}
