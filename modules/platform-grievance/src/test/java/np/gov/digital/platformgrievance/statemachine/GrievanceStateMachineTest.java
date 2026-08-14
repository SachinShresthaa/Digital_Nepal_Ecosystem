package np.gov.digital.platformgrievance.statemachine;

import np.gov.digital.platformgrievance.enums.GrievanceStatus;
import np.gov.digital.platformgrievance.exception.InvalidGrievanceTransitionException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Sachin — Day 8 Week 2.
 * Tests for the state machine — valid transitions and invalid transition rejection.
 * Integration Day 12 checklist: "Confirm every state transition writes an event record."
 */
class GrievanceStateMachineTest {

    @Test
    void receivedToInProgressIsAllowed() {
        assertThatNoException().isThrownBy(() ->
                GrievanceStateMachine.validate(
                        GrievanceStatus.RECEIVED, GrievanceStatus.IN_PROGRESS));
    }

    @Test
    void inProgressToResolvedWardIsAllowed() {
        assertThatNoException().isThrownBy(() ->
                GrievanceStateMachine.validate(
                        GrievanceStatus.IN_PROGRESS, GrievanceStatus.RESOLVED_WARD));
    }

    @Test
    void receivedToResolvedWardDirectlyIsNotAllowed() {
        assertThatThrownBy(() ->
                GrievanceStateMachine.validate(
                        GrievanceStatus.RECEIVED, GrievanceStatus.RESOLVED_WARD))
                .isInstanceOf(InvalidGrievanceTransitionException.class);
    }

    @Test
    void receivedToClosedDirectlyIsNotAllowed() {
        assertThatThrownBy(() ->
                GrievanceStateMachine.validate(
                        GrievanceStatus.RECEIVED, GrievanceStatus.CLOSED))
                .isInstanceOf(InvalidGrievanceTransitionException.class);
    }

    @Test
    void closedInvalidCannotTransitionAnywhere() {
        for (GrievanceStatus target : GrievanceStatus.values()) {
            assertThat(GrievanceStateMachine.isAllowed(
                    GrievanceStatus.CLOSED_INVALID, target))
                    .isFalse();
        }
    }

    @Test
    void receivedToClosedInvalidIsAllowed() {
        assertThat(GrievanceStateMachine.isAllowed(
                GrievanceStatus.RECEIVED, GrievanceStatus.CLOSED_INVALID)).isTrue();
    }

    @Test
    void inProgressToReferredJudicialIsAllowed() {
        assertThat(GrievanceStateMachine.isAllowed(
                GrievanceStatus.IN_PROGRESS, GrievanceStatus.REFERRED_JUDICIAL)).isTrue();
    }

    @Test
    void closedToReopenedIsAllowed() {
        assertThat(GrievanceStateMachine.isAllowed(
                GrievanceStatus.CLOSED, GrievanceStatus.REOPENED)).isTrue();
    }

    @Test
    void reopenedToInProgressIsAllowed() {
        assertThat(GrievanceStateMachine.isAllowed(
                GrievanceStatus.REOPENED, GrievanceStatus.IN_PROGRESS)).isTrue();
    }
}