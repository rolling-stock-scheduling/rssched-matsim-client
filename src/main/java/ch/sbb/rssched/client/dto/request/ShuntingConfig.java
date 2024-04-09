package ch.sbb.rssched.client.dto.request;

/**
 * Shunting configuration
 *
 * @param minimalDuration      the minimum time that is always needed between two activities.
 * @param deadHeadTripDuration the time to change from serviceTrip to DeadHeadTrip.
 * @param couplingDuration     the additional time needed for a vehicle to be coupled or uncoupled and changes route.
 */
record ShuntingConfig(int minimalDuration, int deadHeadTripDuration, int couplingDuration) {
    ShuntingConfig {
        if (minimalDuration < 0 || deadHeadTripDuration < 0 || couplingDuration < 0) {
            throw new IllegalArgumentException("Durations must be non-negative.");
        }
    }
}
