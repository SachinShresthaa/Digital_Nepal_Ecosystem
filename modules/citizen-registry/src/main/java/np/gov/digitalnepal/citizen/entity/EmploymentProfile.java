package np.gov.digitalnepal.citizen.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "employment_profile")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmploymentProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "citizen_id", nullable = false, unique = true)
    private Citizen citizen;

    /**
     * One of 10 employment categories:
     * UNEMPLOYED / SELF_EMPLOYED / FARMER / GOVERNMENT /
     * PRIVATE / FOREIGN / STUDENT / HOMEMAKER / RETIRED / DISABLED_UNABLE
     */
    @Column(name = "category", nullable = false, length = 30)
    private String category;

    /**
     * Category-specific sub-fields stored as JSON string.
     * Example for UNEMPLOYED: {"duration_months": 6, "last_employer": "ABC Co"}
     */
    @Column(name = "sub_fields", columnDefinition = "jsonb")
    private String subFields;

    @Column(name = "income_band", length = 30)
    private String incomeBand;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    // updated_by references users.id
    @Column(name = "updated_by", nullable = false)
    private UUID updatedBy;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public boolean isUnemployed() {
        return "UNEMPLOYED".equalsIgnoreCase(this.category);
    }
}
