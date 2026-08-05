package np.gov.digital.platformsync.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "sync_conflict_registry")
public class SyncConflictRegistry {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "citizen_id", nullable = false)
    private UUID citizenId;

    @Column(name = "submitting_user_id")
    private UUID submittingUserId;

    @Column(name = "device_id", length = 200)
    private String deviceId;

    @Column(name = "server_version", nullable = false)
    private Integer serverVersion;

    @Column(name = "device_version", nullable = false)
    private Integer deviceVersion;

    @Column(name = "conflicting_data", columnDefinition = "jsonb", nullable = false)
    private String conflictingData;

    @Column(name = "resolution_status", nullable = false, length = 30)
    private String resolutionStatus = "PENDING_REVIEW";

    @Column(name = "resolved_by")
    private UUID resolvedBy;

    @Column(name = "resolved_at")
    private Instant resolvedAt;

    // ---------------- Getters & Setters ----------------

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getCitizenId() {
        return citizenId;
    }

    public void setCitizenId(UUID citizenId) {
        this.citizenId = citizenId;
    }

    public UUID getSubmittingUserId() {
        return submittingUserId;
    }

    public void setSubmittingUserId(UUID submittingUserId) {
        this.submittingUserId = submittingUserId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Integer getServerVersion() {
        return serverVersion;
    }

    public void setServerVersion(Integer serverVersion) {
        this.serverVersion = serverVersion;
    }

    public Integer getDeviceVersion() {
        return deviceVersion;
    }

    public void setDeviceVersion(Integer deviceVersion) {
        this.deviceVersion = deviceVersion;
    }

    public String getConflictingData() {
        return conflictingData;
    }

    public void setConflictingData(String conflictingData) {
        this.conflictingData = conflictingData;
    }

    public String getResolutionStatus() {
        return resolutionStatus;
    }

    public void setResolutionStatus(String resolutionStatus) {
        this.resolutionStatus = resolutionStatus;
    }

    public UUID getResolvedBy() {
        return resolvedBy;
    }

    public void setResolvedBy(UUID resolvedBy) {
        this.resolvedBy = resolvedBy;
    }

    public Instant getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(Instant resolvedAt) {
        this.resolvedAt = resolvedAt;
    }
}