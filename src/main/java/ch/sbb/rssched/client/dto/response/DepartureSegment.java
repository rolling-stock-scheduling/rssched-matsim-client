package ch.sbb.rssched.client.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class DepartureSegment {
    private String departureSegment;
    private String origin;
    private String destination;
    private LocalDateTime departure;
    private LocalDateTime arrival;
    private String vehicleType;
    private List<String> formation;
}
