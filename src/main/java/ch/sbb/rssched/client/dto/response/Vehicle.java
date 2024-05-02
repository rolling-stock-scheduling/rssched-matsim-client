package ch.sbb.rssched.client.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class Vehicle {
    private String id;
    private String startDepot;
    private String endDepot;
    private List<VehicleDepartureSegment> departureSegments;
    private List<VehicleMaintenanceSlot> maintenanceSlots;
    private List<VehicleDeadHeadTrip> deadHeadTrips;
}
