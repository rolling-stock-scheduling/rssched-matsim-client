package ch.sbb.rssched.client.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class MaintenanceSlot {
    private String maintenanceSlot;
    private String location;
    private LocalDateTime start;
    private LocalDateTime end;
    private List<String> formation;
}
