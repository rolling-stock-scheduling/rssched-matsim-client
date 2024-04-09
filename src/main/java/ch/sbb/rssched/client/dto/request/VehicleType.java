package ch.sbb.rssched.client.dto.request;

/**
 * @param id                    the vehicle type id.
 * @param capacity              the total capacity including standing room.
 * @param seats                 the number of passenger seats.
 * @param maximalFormationCount the maximal number of vehicle in one formation. Optional, null means unbounded.
 */
record VehicleType(String id, int capacity, int seats,
                   int maximalFormationCount) implements Comparable<VehicleType> {
    VehicleType {
        if (capacity < 0 || seats < 0 || maximalFormationCount < 0) {
            throw new IllegalArgumentException(
                    "Capacity, number of seats and maximal formation count must be non-negative.");
        }
    }

    @Override
    public int compareTo(VehicleType other) {
        return this.id.compareTo(other.id);
    }
}
