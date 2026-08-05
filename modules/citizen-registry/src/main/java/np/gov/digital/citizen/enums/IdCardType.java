package np.gov.digital.citizen.enums;

/**
 * Types of ID cards that can be issued to eligible citizens.
 * MVP covers Unemployment and Disability cards only.
 * Senior, Single Woman, and Farmer cards are Phase 2.
 */
public enum IdCardType {
    UNEMPLOYMENT,   // citizen is unemployed + age >= 18
    DISABILITY,     // disability severity >= 2 + certificate present
    SENIOR,         // Phase 2
    SINGLE_WOMAN,   // Phase 2
    FARMER          // Phase 2
}
