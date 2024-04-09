package ch.sbb.rssched.client.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
final class Location implements Comparable<Location> {
    private final String id;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Integer dayLimit;

    /**
     * @param id       the location id.
     * @param dayLimit the number of vehicles that can be located here at any point during the day. At the end of the
     *                 schedule, vehicles are only allowed to be here, if there is a depot. Optional, null means
     *                 infinity.
     */
    Location(String id, int dayLimit) {
        this(id);
        this.dayLimit = dayLimit;
    }

    /**
     * @param id the location id.
     */
    Location(String id) {
        this.id = id;
    }

    @Override
    public int compareTo(Location other) {
        return this.id.compareTo(other.id);
    }

}