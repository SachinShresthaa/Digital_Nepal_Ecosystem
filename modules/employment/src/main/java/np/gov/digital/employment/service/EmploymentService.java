package np.gov.digital.employment.service;



import np.gov.digital.employment.Enum.EmploymentCategory;
import np.gov.digital.employment.dto.EmploymentRequest;
import np.gov.digital.employment.dto.EmploymentResponse;
import np.gov.digital.employment.entity.EmploymentProfile;
import np.gov.digital.employment.repository.EmploymentRepository;
import np.gov.digital.foreignemployment.service.ForeignEmploymentService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class EmploymentService {
    private final ForeignEmploymentService foreignEmploymentService;

    private final EmploymentRepository repository;

    public EmploymentService(EmploymentRepository repository,ForeignEmploymentService foreignEmploymentService) {
        this.repository = repository;
        this.foreignEmploymentService = foreignEmploymentService;
    }

    public EmploymentResponse createOrUpdate(EmploymentRequest req) {

        EmploymentProfile profile = repository.findByCitizenId(req.citizenId)
                .orElse(new EmploymentProfile());

        profile.setCitizenId(req.citizenId);
        profile.setCategory(req.category);
        profile.setSubFields(req.subFields);
        profile.setIncomeBand(req.incomeBand);
        profile.setUpdatedBy(req.updatedBy);

        EmploymentProfile saved = repository.save(profile);

        // 🔥 IMPORTANT: 10-category logic
        if (req.category == EmploymentCategory.FOREIGN_EMPLOYED) {
            foreignEmploymentService.syncFromEmployment(req);
        }

        return toResponse(saved);
    }

    public EmploymentResponse getByCitizenId(UUID citizenId) {
        EmploymentProfile profile = repository.findByCitizenId(citizenId)
                .orElseThrow(() -> new RuntimeException("Employment profile not found"));

        return toResponse(profile);
    }

    private EmploymentResponse toResponse(EmploymentProfile p) {
        EmploymentResponse res = new EmploymentResponse();
        res.id = p.getId();
        res.citizenId = p.getCitizenId();
        res.category = p.getCategory();
         res.subFields = p.getSubFields();
        res.incomeBand = p.getIncomeBand();
        res.updatedAt = p.getUpdatedAt();
        res.updatedBy = p.getUpdatedBy();
        return res;
    }
}
