package ch.sbb.rssched.client.dto.request;

/**
 * Maintenance configuration
 *
 * @param maximalDistance the maximal distance a vehicle unit can travel without visiting a maintenance slot.
 */
record MaintenanceConfig(int maximalDistance) {
}
