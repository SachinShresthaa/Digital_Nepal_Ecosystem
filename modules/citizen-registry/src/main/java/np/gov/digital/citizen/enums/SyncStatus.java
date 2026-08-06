package np.gov.digital.citizen.enums;

/**
 * Tracks the sync state of a citizen record collected on a Flutter mobile device.
 *
 * PENDING  — record created offline on device, not yet sent to server
 * SYNCED   — record successfully committed to server and confirmed
 * CONFLICT — server and device have different versions; admin review required
 * TO BE FAILED   — sync attempted but failed (network/validation error); will retry
 */
public enum SyncStatus {
    PENDING,
    SYNCED,
    CONFLICT,
    FAILED
}
