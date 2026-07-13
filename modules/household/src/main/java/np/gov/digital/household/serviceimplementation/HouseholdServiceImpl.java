package np.gov.digital.household.serviceimplementation;


import lombok.RequiredArgsConstructor;
import np.gov.digital.household.dto.HouseholdRequest;
import np.gov.digital.household.dto.HouseholdResponse;
import np.gov.digital.household.entity.Household;
import np.gov.digital.household.repository.HouseholdRepository;
import np.gov.digital.household.service.HouseholdService;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HouseholdServiceImpl implements HouseholdService {

    private final HouseholdRepository repository;

    @Override
    public HouseholdResponse create(HouseholdRequest request) {

        Household entity = mapToEntity(request);
        entity.setCreatedAt(OffsetDateTime.now());
        entity.setUpdatedAt(OffsetDateTime.now());

        return mapToResponse(repository.save(entity));
    }

    @Override
    public HouseholdResponse getById(UUID id) {

        Household entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Household not found"));

        return mapToResponse(entity);
    }

    @Override
    public List<HouseholdResponse> getByWardId(UUID wardId) {
        return repository.findByWardId(wardId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<HouseholdResponse> getByHeadCitizenId(UUID headCitizenId) {
        return repository.findByHeadCitizenId(headCitizenId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public HouseholdResponse update(UUID id, HouseholdRequest request) {

        Household entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Household not found"));

        entity.setWardId(request.getWardId());
        entity.setHeadCitizenId(request.getHeadCitizenId());
        entity.setHouseType(request.getHouseType());
        entity.setConstructionType(request.getConstructionType());
        entity.setRoomCount(request.getRoomCount());

        entity.setLandOwned(request.getLandOwned());
        entity.setLandAreaRopani(request.getLandAreaRopani());
        entity.setLandLocation(request.getLandLocation());

        entity.setElectricity(request.getElectricity());
        entity.setWaterSource(request.getWaterSource());
        entity.setSanitation(request.getSanitation());
        entity.setInternetAccess(request.getInternetAccess());

        entity.setHasBankAccount(request.getHasBankAccount());
        entity.setBankName(request.getBankName());

        entity.setMonthlyIncomeBand(request.getMonthlyIncomeBand());
        entity.setAnnualIncomeBand(request.getAnnualIncomeBand());

        entity.setDependentCount(request.getDependentCount());
        entity.setPovertyClass(request.getPovertyClass());

        entity.setUpdatedAt(OffsetDateTime.now());

        return mapToResponse(repository.save(entity));
    }

    @Override
    public void delete(UUID id) {
        repository.deleteById(id);
    }

    // ---------- MAPPERS ----------

    private Household mapToEntity(HouseholdRequest r) {
        return Household.builder()
                .wardId(r.getWardId())
                .headCitizenId(r.getHeadCitizenId())
                .houseType(r.getHouseType())
                .constructionType(r.getConstructionType())
                .roomCount(r.getRoomCount())
                .landOwned(r.getLandOwned())
                .landAreaRopani(r.getLandAreaRopani())
                .landLocation(r.getLandLocation())
                .electricity(r.getElectricity())
                .waterSource(r.getWaterSource())
                .sanitation(r.getSanitation())
                .internetAccess(r.getInternetAccess())
                .hasBankAccount(r.getHasBankAccount())
                .bankName(r.getBankName())
                .monthlyIncomeBand(r.getMonthlyIncomeBand())
                .annualIncomeBand(r.getAnnualIncomeBand())
                .dependentCount(r.getDependentCount())
                .povertyClass(r.getPovertyClass())
                .build();
    }

    private HouseholdResponse mapToResponse(Household e) {

        HouseholdResponse r = new HouseholdResponse();

        r.setId(e.getId());
        r.setWardId(e.getWardId());
        r.setHeadCitizenId(e.getHeadCitizenId());
        r.setHouseType(e.getHouseType());
        r.setConstructionType(e.getConstructionType());
        r.setRoomCount(e.getRoomCount());
        r.setLandOwned(e.getLandOwned());
        r.setLandAreaRopani(e.getLandAreaRopani());
        r.setLandLocation(e.getLandLocation());
        r.setElectricity(e.getElectricity());
        r.setWaterSource(e.getWaterSource());
        r.setSanitation(e.getSanitation());
        r.setInternetAccess(e.getInternetAccess());
        r.setHasBankAccount(e.getHasBankAccount());
        r.setBankName(e.getBankName());
        r.setMonthlyIncomeBand(e.getMonthlyIncomeBand());
        r.setAnnualIncomeBand(e.getAnnualIncomeBand());
        r.setDependentCount(e.getDependentCount());
        r.setPovertyClass(e.getPovertyClass());
        r.setCreatedAt(e.getCreatedAt());
        r.setUpdatedAt(e.getUpdatedAt());

        return r;
    }
}
