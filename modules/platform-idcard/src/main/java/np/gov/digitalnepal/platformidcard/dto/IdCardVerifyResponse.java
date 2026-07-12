package np.gov.digitalnepal.platformidcard.dto;

public record IdCardVerifyResponse(
        String  status,      // VALID or INVALID
        String  name,        // citizen name (only PII returned)
        String  cardType,    // DISABILITY / UNEMPLOYMENT
        String  issuedDate,
        String  ward,
        String  reason       // null if VALID, reason if INVALID
) {}
