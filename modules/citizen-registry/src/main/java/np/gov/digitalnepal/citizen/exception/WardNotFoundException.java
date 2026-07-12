package np.gov.digitalnepal.citizen.exception;

import java.util.UUID;

public class WardNotFoundException extends RuntimeException {
    public WardNotFoundException(UUID wardId) {
        super("Ward not found with ID: " + wardId);
    }
}
