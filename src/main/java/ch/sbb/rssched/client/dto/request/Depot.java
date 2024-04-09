package ch.sbb.rssched.client.dto.request;

import lombok.AccessLevel;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
class Depot implements Comparable<Depot> {
    private final String id;
    private final String location;
    private final int capacity;
    private final List<AllowedType> allowedTypes = new ArrayList<>();
    @Getter(AccessLevel.NONE)
    private final Set<String> vehicleTypes = new HashSet<>(); // internal

    /**
     * @param id       the depot id.
     * @param location the location id of the depot.
     * @param capacity the total capacity at depot; limits the number of vehicles at the start (and end) of the
     *                 schedule.
     */
    Depot(String id, String location, int capacity) {
        this.id = id;
        this.location = location;
        this.capacity = capacity;
    }

    void addUpperBoundForVehicleType(String vehicleTypeId, int upperBound) {
        if (vehicleTypes.contains(vehicleTypeId)) {
            throw new IllegalArgumentException(
                    "Upper bound for vehicle type with ID " + vehicleTypeId + " already set.");
        }
        AllowedType newAllowedType = new AllowedType(vehicleTypeId, upperBound);
        int index = Collections.binarySearch(allowedTypes, newAllowedType);
        if (index < 0) {
            index = -(index + 1);
        }
        allowedTypes.add(index, newAllowedType);
        vehicleTypes.add(vehicleTypeId);
    }

    boolean hasNoVehicleTypesAssigned() {
        return allowedTypes.isEmpty();
    }

    boolean supportsVehicleType(String vehicleTypeId) {
        AllowedType targetAllowedType = new AllowedType(vehicleTypeId, 0);
        int index = Collections.binarySearch(allowedTypes, targetAllowedType);
        return index >= 0;
    }

    @Override
    public int compareTo(Depot other) {
        return this.id.compareTo(other.id);
    }
}
