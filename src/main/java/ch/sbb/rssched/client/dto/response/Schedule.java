package ch.sbb.rssched.client.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class Schedule {
    private List<DepotLoad> depotLoads;
    private List<Fleet> fleet;
    private List<DepartureSegment> departureSegments;
    private List<MaintenanceSlot> maintenanceSlots;
    private List<DeadHeadTrip> deadHeadTrips;
}
