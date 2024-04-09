package ch.sbb.rssched.client.dto.request;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
class Route implements Comparable<Route> {
    private final String id;
    private final String vehicleType;
    private final List<RouteSegment> segments = new ArrayList<>();

    Route(String id, String vehicleType) {
        this.id = id;
        this.vehicleType = vehicleType;
    }

    @Override
    public int compareTo(Route other) {
        return this.id.compareTo(other.id);
    }

    public RouteSegment addSegment(String id, String originId, String destinationId, int distance, int duration, int maximalFormationCount) {
        RouteSegment segment = new RouteSegment(id, segments.size(), originId, destinationId, distance, duration,
                maximalFormationCount);
        segments.add(segment);
        return segment;
    }

    public RouteSegment addSegment(String id, String originId, String destinationId, int distance, int duration) {
        RouteSegment segment = new RouteSegment(id, segments.size(), originId, destinationId, distance, duration);
        segments.add(segment);
        return segment;
    }

}
