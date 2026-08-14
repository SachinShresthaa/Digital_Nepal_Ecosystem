package np.gov.digital.platformgrievance.statemachine;

import np.gov.digital.platformgrievance.enums.GrievanceStatus;
import np.gov.digital.platformgrievance.exception.InvalidGrievanceTransitionException;

import java.util.Map;
import java.util.Set;

public final class GrievanceStateMachine {

    private GrievanceStateMachine() {}

    private static final Map<GrievanceStatus, Set<GrievanceStatus>> ALLOWED =
            Map.of(
                    GrievanceStatus.RECEIVED,         Set.of(
                            GrievanceStatus.IN_PROGRESS,
                            GrievanceStatus.CLOSED_INVALID
                    ),
                    GrievanceStatus.IN_PROGRESS,      Set.of(
                            GrievanceStatus.RESOLVED_WARD,
                            GrievanceStatus.REFERRED_JUDICIAL
                    ),
                    GrievanceStatus.RESOLVED_WARD,    Set.of(
                            GrievanceStatus.CLOSED,
                            GrievanceStatus.REOPENED
                    ),
                    GrievanceStatus.REFERRED_JUDICIAL, Set.of(
                            GrievanceStatus.RESOLVED_JUDICIAL,
                            GrievanceStatus.REFERRED_BOARD
                    ),
                    GrievanceStatus.RESOLVED_JUDICIAL, Set.of(
                            GrievanceStatus.CLOSED,
                            GrievanceStatus.REOPENED
                    ),
                    GrievanceStatus.REFERRED_BOARD,   Set.of(
                            GrievanceStatus.RESOLVED_BOARD,
                            GrievanceStatus.REFERRED_COMMISSION
                    ),
                    GrievanceStatus.RESOLVED_BOARD,   Set.of(GrievanceStatus.CLOSED),
                    GrievanceStatus.CLOSED,           Set.of(GrievanceStatus.REOPENED),
                    GrievanceStatus.REOPENED,         Set.of(GrievanceStatus.IN_PROGRESS),
                    GrievanceStatus.CLOSED_INVALID,   Set.of()
            );

    public static void validate(GrievanceStatus from, GrievanceStatus to) {
        Set<GrievanceStatus> allowed = ALLOWED.getOrDefault(from, Set.of());
        if (!allowed.contains(to)) {
            throw new InvalidGrievanceTransitionException(from, to);
        }
    }
    public static boolean isAllowed(GrievanceStatus from, GrievanceStatus to) {
        return ALLOWED.getOrDefault(from, Set.of()).contains(to);
    }
}