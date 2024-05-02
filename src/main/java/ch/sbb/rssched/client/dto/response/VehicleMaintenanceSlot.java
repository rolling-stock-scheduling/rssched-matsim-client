package ch.sbb.rssched.client.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VehicleMaintenanceSlot {
    private String maintenanceSlot;
    private String location;
    private LocalDateTime start;
    private LocalDateTime end;
}
