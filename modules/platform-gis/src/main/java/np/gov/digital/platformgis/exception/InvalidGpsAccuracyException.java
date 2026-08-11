package np.gov.digital.platformgis.exception;

public class InvalidGpsAccuracyException extends RuntimeException {

    public InvalidGpsAccuracyException(int reportedAccuracyM, int maxAllowedM) {
        super("GPS reading rejected: accuracy " + reportedAccuracyM +
                "m exceeds the maximum allowed " + maxAllowedM + "m. " +
                "Re-capture location with better signal before submitting.");
    }
}