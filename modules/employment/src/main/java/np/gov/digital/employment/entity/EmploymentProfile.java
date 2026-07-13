package np.gov.digital.employment.entity;



import jakarta.persistence.*;
import lombok.*;
import np.gov.digital.employment.Enum.EmploymentCategory;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;
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
    @GeneratedValue
    private UUID id;

    @Column(name = "citizen_id", nullable = false, unique = true)
    private UUID citizenId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmploymentCategory category;

    // JSONB mapping (VERY IMPORTANT)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "sub_fields", columnDefinition = "jsonb")
    private Map<String, Object> subFields;

    private String incomeBand;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "updated_by")
    private UUID updatedBy;

    @PrePersist
    public void prePersist() {
        this.updatedAt = Instant.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
    }
}