package np.gov.digital.platformgis.entity;

import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Point;

import java.time.Instant;
import java.util.UUID;
@Entity
@Table(name = "citizen_gis")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CitizenGis {

    @Id
    @Column(name = "citizen_id", nullable = false, updatable = false)
    private UUID citizenId;

    // Denormalized for RLS — must always mirror citizen.ward_id
    @Column(name = "ward_id", nullable = false)
    private UUID wardId;

    @Column(name = "location", nullable = false, columnDefinition = "geometry(Point,4326)")
    private Point location;

    @Column(name = "location_accuracy_m")
    private Short locationAccuracyM;

    @Column(name = "elevation_m")
    private Short elevationM;

    // FLOOD / LANDSLIDE / EARTHQUAKE / FIRE / NONE — optional, manual entry MVP
    @Column(name = "risk_zone", length = 30)
    private String riskZone;

    @Column(name = "road_type_to_highway", length = 30)
    private String roadTypeToHighway;

    // --- Phase 2 columns — present on the entity, never written by this module ---
    @Column(name = "dist_health_post_km")
    private java.math.BigDecimal distHealthPostKm;

    @Column(name = "time_health_post_min")
    private Short timeHealthPostMin;

    @Column(name = "dist_school_km")
    private java.math.BigDecimal distSchoolKm;

    @Column(name = "time_school_min")
    private Short timeSchoolMin;

    @Column(name = "dist_market_km")
    private java.math.BigDecimal distMarketKm;

    @Column(name = "dist_bank_km")
    private java.math.BigDecimal distBankKm;
    // --- end Phase 2 columns ---

    @Column(name = "captured_by")
    private UUID capturedBy;

    @Column(name = "captured_at", nullable = false)
    @Builder.Default
    private Instant capturedAt = Instant.now();
}