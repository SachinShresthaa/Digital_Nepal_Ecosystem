package np.gov.digital.platformgis.repository;

import np.gov.digital.platformgis.entity.CitizenGis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CitizenGisRepository extends JpaRepository<CitizenGis, UUID> {
    // citizenId is the PK — findById / existsById already cover the
    // 1:1 lookup pattern this module needs. Add ward-scoped queries
    // here in Week 3 if the map-view endpoint needs them.
}