package np.gov.digital.citizen.enums;

/**
 * How citizen consent was obtained during registration.
 * Recorded with timestamp in citizen.consent_recorded_at.
 * Required by Nepal's Individual Privacy Act 2018 and Constitution Article 28.
 */
public enum ConsentChannel {
    WARD_OFFICE,   // Citizen visited ward office in person
    FIELD,         // Ward Admin collected data in the field (Flutter mobile app)
    PORTAL         // Citizen submitted via self-service portal
}
