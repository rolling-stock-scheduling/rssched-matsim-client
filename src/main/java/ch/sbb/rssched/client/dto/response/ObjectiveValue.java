package ch.sbb.rssched.client.dto.response;

import lombok.Data;

@Data
public class ObjectiveValue {
    private long unservedPassengers;
    private long maintenanceViolation;
    private int vehicleCount;
    private long costs;
}
