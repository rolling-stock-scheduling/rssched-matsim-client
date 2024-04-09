package ch.sbb.rssched.client.dto.request;

import java.time.LocalDateTime;

/**
 * @param id       the id of the maintenance slot.
 * @param location the corresponding location id of the slot.
 * @param start    the start time of the slot.
 * @param end      the end time of the slot.
 */
public record MaintenanceSlot(String id, String location, LocalDateTime start,
                              LocalDateTime end) implements Comparable<MaintenanceSlot> {
    @Override
    public int compareTo(MaintenanceSlot o) {
        return start.compareTo(o.start);
    }
}
