package ch.sbb.rssched.client.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VehicleDeadHeadTrip {
    private String id;
    private String origin;
    private String destination;
    private LocalDateTime departure;
    private LocalDateTime arrival;
}
