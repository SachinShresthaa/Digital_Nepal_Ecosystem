package np.gov.digital.platformsync.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public class SyncBatchRequestDTO {

    @NotNull
    private UUID batchId;

    @NotNull
    private UUID wardId;

    @NotNull
    private UUID submittedBy;

    @NotBlank
    private String deviceId;

    @Valid
    @NotEmpty
    private List<CitizenRecordDTO> records;

    // ---------------- Getters & Setters ----------------

    public UUID getBatchId() {
        return batchId;
    }

    public void setBatchId(UUID batchId) {
        this.batchId = batchId;
    }

    public UUID getWardId() {
        return wardId;
    }

    public void setWardId(UUID wardId) {
        this.wardId = wardId;
    }

    public UUID getSubmittedBy() {
        return submittedBy;
    }

    public void setSubmittedBy(UUID submittedBy) {
        this.submittedBy = submittedBy;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public List<CitizenRecordDTO> getRecords() {
        return records;
    }

    public void setRecords(List<CitizenRecordDTO> records) {
        this.records = records;
    }
}