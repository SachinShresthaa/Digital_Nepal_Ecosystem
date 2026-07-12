package np.gov.digitalnepal.citizen.entity;

import jakarta.persistence.*;
import lombok.*;
import np.gov.digitalnepal.citizen.enums.LinkStatus;

import np.gov.digitalnepal.citizen.enums.RelationType;
import java.time.Instant;
import java.util.UUID;

//Represents a family relationship edge between two citizens.
@Entity
@Table(
        name = "family_link",
        indexes = {
                @Index(name = "idx_family_link_citizen", columnList = "citizen_id"),
                @Index(name = "idx_family_link_related", columnList = "related_citizen_id"),
                @Index(name = "idx_family_link_cit_norm", columnList = "related_citizenship_no")
        }
        )
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FamilyLink {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /**
     * The citizen who owns this link.
     * Example: if citizen A says their father is B, citizen_id = A.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "citizen_id", nullable = false)
    private Citizen citizen;

    /**
     * Type of relationship: FATHER / MOTHER / SPOUSE / CHILD
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "relation_type", nullable = false, length = 30)
    private RelationType relationType;

    /**
     * The related citizen if they are already registered in the system.
     * NULL if the related person has not registered yet (link_status = PENDING).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_citizen_id")
    private Citizen relatedCitizen;

    /**
     * Name of the related person as provided during registration.
     * Stored even when related_citizen_id is set — used for display.
     */
    @Column(name = "related_name_text", length = 300)
    private String relatedNameText;

    /**
     * Normalized citizenship number of the related person (alphanumeric only).
     * Used to auto-link when the related person registers later.
     * Matched against citizen.citizenship_no_norm.
     */
    @Column(name = "related_citizenship_no", length = 100)
    private String relatedCitizenshipNo;

    /**
     * PENDING  — related person not registered yet, waiting for auto-link
     * LINKED   — both citizens exist and confirmed linked
     * UNRESOLVABLE — could not be matched
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "link_status", nullable = false, length = 20)
    @Builder.Default
    private LinkStatus linkStatus = LinkStatus.PENDING;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }
}
