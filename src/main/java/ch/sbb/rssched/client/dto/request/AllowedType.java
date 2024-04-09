package ch.sbb.rssched.client.dto.request;

record AllowedType(String vehicleType, int capacity) implements Comparable<AllowedType> {
    AllowedType {
        if (capacity < 0) {
            throw new IllegalArgumentException("Capacity must be non-negative.");
        }
    }

    @Override
    public int compareTo(AllowedType other) {
        return this.vehicleType.compareTo(other.vehicleType);
    }
}