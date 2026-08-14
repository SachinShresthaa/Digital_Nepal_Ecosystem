package np.gov.digital.platformgrievance.exception;

import np.gov.digital.platformgrievance.enums.GrievanceStatus;

public class InvalidGrievanceTransitionException extends RuntimeException {

    public InvalidGrievanceTransitionException(GrievanceStatus from, GrievanceStatus to) {
        super("Invalid grievance transition: " + from + " → " + to +
                ". This move is not permitted by the state machine.");
    }
}