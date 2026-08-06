package np.gov.digital.citizen.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import np.gov.digital.citizen.dto.EligibilityResponse;
import np.gov.digital.citizen.entity.Citizen;
import np.gov.digital.citizen.entity.DisabilityProfile;
import np.gov.digital.citizen.entity.EmploymentProfile;
import np.gov.digital.citizen.enums.IdCardType;
import np.gov.digital.citizen.repository.CitizenRepository;
import np.gov.digital.citizen.repository.DisabilityProfileRepository;
import np.gov.digital.citizen.repository.EmploymentProfileRepository;
import np.gov.digital.citizen.util.NidEncryptionUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EligibilityService {

    private final CitizenRepository citizenRepository;
    private final DisabilityProfileRepository disabilityProfileRepository;
    private final EmploymentProfileRepository employmentProfileRepository;
    private final NidEncryptionUtil nidEncryptionUtil;

    @Transactional(readOnly = true)
    public EligibilityResponse evaluate(UUID citizenId) {
        Citizen citizen = citizenRepository.findById(citizenId)
                .orElseThrow(() -> new RuntimeException("Citizen not found: " + citizenId));

        List<EligibilityResponse.EligibilityResult> results = new ArrayList<>();
        results.add(evaluateDisabilityRule(citizen));
        results.add(evaluateUnemploymentRule(citizen));

        List<EligibilityResponse.EligibilityResult> eligibleCards = results.stream()
                .filter(EligibilityResponse.EligibilityResult::isEligible)
                .toList();

        log.info("Eligibility evaluated — citizenId: {}, eligible for {} programmes",
                citizenId, eligibleCards.size());

        return EligibilityResponse.builder()
                .citizenId(citizen.getId())
                .nameNp(citizen.getNameNp())
                .nameEn(citizen.getNameEn())
                .eligibleCards(eligibleCards)
                .hasEligibility(!eligibleCards.isEmpty())
                .totalEligible(eligibleCards.size())
                .build();
    }

    private EligibilityResponse.EligibilityResult evaluateDisabilityRule(Citizen citizen) {
        Optional<DisabilityProfile> profileOpt =
                disabilityProfileRepository.findByCitizenId(citizen.getId());

        if (profileOpt.isEmpty()) {
            return EligibilityResponse.EligibilityResult.builder()
                    .cardType(IdCardType.DISABILITY)
                    .eligible(false)
                    .ineligibilityReason("No disability profile registered")
                    .build();
        }

        DisabilityProfile profile = profileOpt.get();

        if (profile.getMaxSeverity() < 2) {
            return EligibilityResponse.EligibilityResult.builder()
                    .cardType(IdCardType.DISABILITY)
                    .eligible(false)
                    .ineligibilityReason("Disability severity below moderate. Current: "
                            + profile.getMaxSeverity())
                    .build();
        }

        if (!profile.hasCertificate()) {
            return EligibilityResponse.EligibilityResult.builder()
                    .cardType(IdCardType.DISABILITY)
                    .eligible(false)
                    .ineligibilityReason("Disability certificate not present")
                    .build();
        }

        return EligibilityResponse.EligibilityResult.builder()
                .cardType(IdCardType.DISABILITY)
                .eligible(true)
                .reason("Severity " + profile.getMaxSeverity()
                        + " and certificate " + profile.getCertificateNo() + " present")
                .build();
    }

    private EligibilityResponse.EligibilityResult evaluateUnemploymentRule(Citizen citizen) {
        Optional<EmploymentProfile> profileOpt =
                employmentProfileRepository.findByCitizenId(citizen.getId());

        if (profileOpt.isEmpty()) {
            return EligibilityResponse.EligibilityResult.builder()
                    .cardType(IdCardType.UNEMPLOYMENT)
                    .eligible(false)
                    .ineligibilityReason("No employment profile registered")
                    .build();
        }

        EmploymentProfile profile = profileOpt.get();

        if (!profile.isUnemployed()) {
            return EligibilityResponse.EligibilityResult.builder()
                    .cardType(IdCardType.UNEMPLOYMENT)
                    .eligible(false)
                    .ineligibilityReason("Employment status is " + profile.getCategory()
                            + ". Must be UNEMPLOYED.")
                    .build();
        }

        int age = calculateAge(citizen);
        if (age < 18) {
            return EligibilityResponse.EligibilityResult.builder()
                    .cardType(IdCardType.UNEMPLOYMENT)
                    .eligible(false)
                    .ineligibilityReason("Age is " + age + ". Must be 18 or older.")
                    .build();
        }

        return EligibilityResponse.EligibilityResult.builder()
                .cardType(IdCardType.UNEMPLOYMENT)
                .eligible(true)
                .reason("UNEMPLOYED and age " + age + " >= 18")
                .build();
    }

    private int calculateAge(Citizen citizen) {
        try {
            String dobPlaintext = nidEncryptionUtil.decrypt(citizen.getDobEnc());
            LocalDate dob = LocalDate.parse(dobPlaintext);
            return Period.between(dob, LocalDate.now()).getYears();
        } catch (Exception e) {
            log.error("Failed to decrypt DOB for citizen: {}", citizen.getId());
            return -1;
        }
    }
}