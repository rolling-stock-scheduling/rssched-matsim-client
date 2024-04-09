package ch.sbb.rssched.client.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
public class RouteSegment implements Comparable<RouteSegment> {
    private final String id;
    private final int order;
    private final String origin;
    private final String destination;
    private final int distance;
    private final int duration;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Integer maximalFormationCount;

    /**
     * Constructor for Segment with maximal formation count.
     *
     * @param order                 the position in the route, starting from 0.
     * @param origin                the id of the origin location.
     * @param destination           the id of the destination location.
     * @param distance              the total distance traveled on the segment.
     * @param duration              the total duration traveled on the segment.
     * @param maximalFormationCount the maximal number of vehicles in one formation on this segment.
     */
    public RouteSegment(String id, int order, String origin, String destination, int distance, int duration, int maximalFormationCount) {
        this(id, order, origin, destination, distance, duration);
        if (maximalFormationCount < 0) {
            throw new IllegalArgumentException("Maximum formation count must be non-negative.");
        }
        this.maximalFormationCount = maximalFormationCount;
    }

    /**
     * Constructor for Segment with maximal formation count.
     *
     * @param order       the position in the route, starting from 0.
     * @param origin      the id of the origin location.
     * @param destination the id of the destination location.
     * @param distance    the total distance traveled on the segment.
     * @param duration    the total duration traveled on the segment.
     */
    public RouteSegment(String id, int order, String origin, String destination, int distance, int duration) {
        if (order < 0 || distance < 0 || duration < 0) {
            throw new IllegalArgumentException("Travel distance, duration and order must be non-negative.");
        }
        this.id = id;
        this.order = order;
        this.origin = origin;
        this.destination = destination;
        this.distance = distance;
        this.duration = duration;
        this.maximalFormationCount = null;
    }

    @Override
    public int compareTo(RouteSegment o) {
        return Integer.compare(order, o.order);
    }

}
