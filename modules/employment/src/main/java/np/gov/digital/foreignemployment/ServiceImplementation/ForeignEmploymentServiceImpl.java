package np.gov.digital.foreignemployment.ServiceImplementation;



import lombok.RequiredArgsConstructor;
import np.gov.digital.employment.dto.EmploymentRequest;
import np.gov.digital.foreignemployment.dto.ForeignEmploymentRequest;
import np.gov.digital.foreignemployment.dto.ForeignEmploymentResponse;
import np.gov.digital.foreignemployment.entity.ForeignEmployment;
import np.gov.digital.foreignemployment.repository.ForeignEmploymentRepository;
import np.gov.digital.foreignemployment.service.ForeignEmploymentService;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ForeignEmploymentServiceImpl implements ForeignEmploymentService {

    private final ForeignEmploymentRepository repository;

    @Override
    public ForeignEmploymentResponse create(ForeignEmploymentRequest request) {

        ForeignEmployment entity = mapToEntity(request);
        entity.setCreatedAt(OffsetDateTime.now());

        return mapToResponse(repository.save(entity));
    }

    @Override
    public ForeignEmploymentResponse getById(UUID id) {

        ForeignEmployment entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Foreign employment not found"));

        return mapToResponse(entity);
    }

    @Override
    public List<ForeignEmploymentResponse> getByCitizenId(UUID citizenId) {

        return repository.findByCitizenId(citizenId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public ForeignEmploymentResponse update(UUID id, ForeignEmploymentRequest request) {

        ForeignEmployment entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Foreign employment not found"));

        entity.setCountryCode(request.getCountryCode());
        entity.setCountryName(request.getCountryName());
        entity.setVisaType(request.getVisaType());
        entity.setEmployerName(request.getEmployerName());
        entity.setJobCategory(request.getJobCategory());
        entity.setDepartureDate(request.getDepartureDate());
        entity.setExpectedReturn(request.getExpectedReturn());
        entity.setRemittanceBand(request.getRemittanceBand());
        entity.setRemittanceChannel(request.getRemittanceChannel());
        entity.setForeignPhoneEnc(request.getForeignPhoneEnc());
        entity.setManagerCitizenId(request.getManagerCitizenId());
        entity.setDoeRegistered(request.getDoeRegistered());
        entity.setInsured(request.getInsured());
        entity.setIsActive(request.getIsActive());
        entity.setReturnDateActual(request.getReturnDateActual());
        entity.setReturnReason(request.getReturnReason());

        return mapToResponse(repository.save(entity));
    }

    @Override
    public void delete(UUID id) {
        repository.deleteById(id);
    }

    // ---------- MAPPERS ----------

    private ForeignEmployment mapToEntity(ForeignEmploymentRequest r) {
        return ForeignEmployment.builder()
                .citizenId(r.getCitizenId())
                .countryCode(r.getCountryCode())
                .countryName(r.getCountryName())
                .visaType(r.getVisaType())
                .employerName(r.getEmployerName())
                .jobCategory(r.getJobCategory())
                .departureDate(r.getDepartureDate())
                .expectedReturn(r.getExpectedReturn())
                .remittanceBand(r.getRemittanceBand())
                .remittanceChannel(r.getRemittanceChannel())
                .foreignPhoneEnc(r.getForeignPhoneEnc())
                .managerCitizenId(r.getManagerCitizenId())
                .doeRegistered(r.getDoeRegistered())
                .insured(r.getInsured())
                .isActive(r.getIsActive() != null ? r.getIsActive() : true)
                .returnDateActual(r.getReturnDateActual())
                .returnReason(r.getReturnReason())
                .build();
    }

    private ForeignEmploymentResponse mapToResponse(ForeignEmployment e) {

        ForeignEmploymentResponse r = new ForeignEmploymentResponse();

        r.setId(e.getId());
        r.setCitizenId(e.getCitizenId());
        r.setCountryCode(e.getCountryCode());
        r.setCountryName(e.getCountryName());
        r.setVisaType(e.getVisaType());
        r.setEmployerName(e.getEmployerName());
        r.setJobCategory(e.getJobCategory());
        r.setDepartureDate(e.getDepartureDate());
        r.setExpectedReturn(e.getExpectedReturn());
        r.setRemittanceBand(e.getRemittanceBand());
        r.setRemittanceChannel(e.getRemittanceChannel());
        r.setForeignPhoneEnc(e.getForeignPhoneEnc());
        r.setManagerCitizenId(e.getManagerCitizenId());
        r.setDoeRegistered(e.getDoeRegistered());
        r.setInsured(e.getInsured());
        r.setIsActive(e.getIsActive());
        r.setReturnDateActual(e.getReturnDateActual());
        r.setReturnReason(e.getReturnReason());
        r.setCreatedAt(e.getCreatedAt());

        return r;
    }
    @Override

    public void syncFromEmployment(EmploymentRequest req) {

        if (req.getSubFields() == null) return;

        Map<String, Object> data = req.getSubFields();

        ForeignEmployment fe = new ForeignEmployment();

        fe.setCitizenId(req.getCitizenId());
        fe.setCountryName(data.get("country") != null ? data.get("country").toString() : null);
        fe.setVisaType(data.get("visaType") != null ? data.get("visaType").toString() : null);
        fe.setEmployerName(data.get("employer") != null ? data.get("employer").toString() : null);

        fe.setIsActive(true);

        repository.save(fe);
    }
}
