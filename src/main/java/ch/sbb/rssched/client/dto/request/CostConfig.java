package ch.sbb.rssched.client.dto.request;

/**
 * Costs are always per second
 *
 * @param staff        each train formation on a service trip has to pay this per second (not for dead-head-trips / idle
 *                     / maintenance)
 * @param serviceTrip  train formation with k vehicles has to pay this k times per minute on a service trip
 * @param maintenance
 * @param deadHeadTrip
 * @param idle
 */
record CostConfig(int staff, int serviceTrip, int maintenance, int deadHeadTrip, int idle) {
}

