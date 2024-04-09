package ch.sbb.rssched.client.dto.request;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Departure implements Comparable<Departure> {
    private final String id;
    private final String route;
    private final List<DepartureSegment> segments = new ArrayList<>();

    /**
     * @param id    the departure id.
     * @param route the id of the corresponding route.
     */
    public Departure(String id, String route) {
        this.id = id;
        this.route = route;
    }

    public DepartureSegment addSegment(String id, String routeSegment, LocalDateTime departure, int passengers, int seated) {
        DepartureSegment segment = new DepartureSegment(id, routeSegment, departure, passengers, seated);
        segments.add(segment);
        return segment;
    }

    private LocalDateTime getEarliestDepartureTime() {
        return segments.stream().map(DepartureSegment::departure).min(LocalDateTime::compareTo)
                .orElse(LocalDateTime.MAX);
    }

    @Override
    public int compareTo(Departure other) {
        return this.getEarliestDepartureTime().compareTo(other.getEarliestDepartureTime());
    }

}
