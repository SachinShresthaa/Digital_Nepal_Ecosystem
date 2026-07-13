package np.gov.digitalnepal.citizen.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "ward",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_ward_municipality_no",
                columnNames = {"municipality_id", "ward_no"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ward {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "municipality_id", nullable = false)
    private Municipality municipality;

    @Column(name = "ward_no", nullable = false)
    private Integer wardNo;

    @Column(name = "name_np", nullable = false, length = 200)
    private String nameNp;

    @Column(name = "name_en", nullable = false, length = 200)
    private String nameEn;

    @Column(name = "population_estimate")
    private Integer populationEstimate;

    @Column(name = "area_sq_km", precision = 8, scale = 3)
    private BigDecimal areaSqKm;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }
}
