package np.gov.digital.platformgrievance.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "grievance_event",
        indexes = {
                @Index(name = "idx_grev_event_grievance", columnList = "grievance_id"),
                @Index(name = "idx_grev_event_citizen",   columnList = "citizen_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GrievanceEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "grievance_id", nullable = false)
    private UUID grievanceId;

    @Column(name = "citizen_id", nullable = false)
    private UUID citizenId;

    // e.g. GRIEVANCE_IN_PROGRESS, GRIEVANCE_RESOLVED_WARD etc.
    @Column(name = "event_type", nullable = false, length = 80)
    private String eventType;

    @Column(name = "old_status", length = 30)
    private String oldStatus;

    @Column(name = "new_status", length = 30)
    private String newStatus;

    @Column(name = "actor_id")
    private UUID actorId;

    @Column(name = "actor_role", length = 30)
    private String actorRole;

    // resolution text, rejection reason, note from the actor
    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();
}