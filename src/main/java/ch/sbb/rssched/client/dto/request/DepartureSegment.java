package ch.sbb.rssched.client.dto.request;

import java.time.LocalDateTime;

/**
 * @param id           the departure segment id.
 * @param routeSegment the id of the segment on the route.
 * @param departure    the time of the departure. It is assumed that a vehicles can serve all segments in order, even
 *                     with shunting between segments.
 * @param passengers   the number of passengers.
 * @param seated       the number of seated passengers.
 */
public record DepartureSegment(String id, String routeSegment, LocalDateTime departure, int passengers, int seated) {
}
