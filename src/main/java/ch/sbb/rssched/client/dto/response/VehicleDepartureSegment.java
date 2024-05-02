package ch.sbb.rssched.client.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VehicleDepartureSegment {
    private String departureSegment;
    private String origin;
    private String destination;
    private LocalDateTime departure;
    private LocalDateTime arrival;
}
