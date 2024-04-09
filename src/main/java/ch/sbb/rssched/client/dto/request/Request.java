package ch.sbb.rssched.client.dto.request;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.time.LocalDateTime;

/**
 * Scheduler request
 * <p>
 * Components: Vehicle types, locations, depots with capacities, routes, service trips, and dead head trips. It provides
 * methods to build and serialize the request in JSON format.
 *
 * @author munterfi
 */
public interface Request {

    /**
     * Creates a new Request builder instance to construct a scheduler request.
     *
     * @return a new Request.Builder instance
     */
    static Request.Builder builder() {
        return new RequestBuilderImpl();
    }

    /**
     * Serializes the Request object to its JSON representation.
     *
     * @return the JSON representation of the Request object
     * @throws JsonProcessingException if there is an error during JSON processing
     */
    String toJSON() throws JsonProcessingException;

    /**
     * The Builder interface provides methods to add various components to the scheduler request and construct the final
     * Request object.
     */
    interface Builder {
        /**
         * Adds a location to the scheduler request.
         *
         * @param id the ID of the location
         * @return the Builder instance
         */
        Request.Builder addLocation(String id);

        /**
         * Adds a location to the scheduler request.
         *
         * @param id       the ID of the location
         * @param dayLimit the number of vehicles that can be located here at any point during the day
         * @return the Builder instance
         */
        Builder addLocation(String id, int dayLimit);

        /**
         * Adds a vehicle type to the scheduler request.
         *
         * @param id                    the ID of the vehicle type
         * @param capacity              the capacity of passengers in the vehicle type
         * @param seats                 the number of seats in the vehicle type
         * @param maximalFormationCount the maximal number of vehicle in one formation
         * @return the Builder instance
         */
        Request.Builder addVehicleType(String id, int capacity, int seats, int maximalFormationCount);

        /**
         * Adds a depot to the scheduler request.
         *
         * @param id         the ID of the depot
         * @param locationId the ID of the location where the depot is located
         * @param capacity   the total capacity of the depot
         * @return the Builder instance
         */
        Builder addDepot(String id, String locationId, int capacity);

        /**
         * Associates a vehicle type with a depot and sets an upper bound on the number of vehicles of that type in the
         * depot.
         *
         * @param depotId       the ID of the depot
         * @param vehicleTypeId the ID of the vehicle type
         * @param upperBound    the upper bound on the number of vehicles of that type in the depot
         * @return the Builder instance
         */
        Request.Builder addVehicleTypeToDepot(String depotId, String vehicleTypeId, int upperBound);


        /**
         * @param id            the ID of the route
         * @param vehicleTypeId the ID of the vehicle type covering the route
         * @return the Builder instance
         */
        Builder addRoute(String id, String vehicleTypeId);


        /**
         * Adds a segment a route.
         *
         * @param id                    the id of the segment
         * @param routeId               the id of the route to add the segment to
         * @param originId              the origin location of the segment
         * @param destinationId         the destination location of the segment
         * @param distance              the travel distance of the route in meters
         * @param duration              the travel duration of the route in seconds
         * @param maximalFormationCount the maximal formation length of the route in meters
         * @return the Builder instance
         */
        Builder addSegmentToRoute(String id, String routeId, String originId, String destinationId, int distance, int duration, int maximalFormationCount);

        /**
         * Adds a departure (on a route) to the scheduler request.
         *
         * @param id      the ID of the departure
         * @param routeId the ID of the route to depart on
         * @return the Builder instance
         */
        Builder addDeparture(String id, String routeId);

        /**
         * Adds a departure segment (on a route segment) to the scheduler request.
         *
         * @param id             the ID of the departure segment
         * @param departureId    the departure to add a segment to
         * @param routeSegmentId the ID of the route segment to depart on
         * @param departureTime  the departure time of the service trip
         * @param passengers     the passenger demand of the service trip
         * @param seats          the maximum amount of concurrently seated passengers
         * @return the Builder instance
         */
        Builder addSegmentToDeparture(String id, String departureId, String routeSegmentId, LocalDateTime departureTime, int passengers, int seats);

        /**
         * Adds a maintenance slot to the scheduler request.
         *
         * @param id         the id of the maintenance slot
         * @param locationId the id of the location, the maintenance takes places
         * @param start      the start time of the slot
         * @param end        the end time of the slot
         * @return the Builder instance
         */
        Builder addMaintenanceSlot(String id, String locationId, LocalDateTime start, LocalDateTime end);

        /**
         * Adds a deadhead trip (empty vehicle movement) to the scheduler request.
         *
         * @param originId      the ID of the origin location
         * @param destinationId the ID of the destination location
         * @param duration      the travel time duration of the deadhead trip in seconds
         * @param distance      the distance of the deadhead trip in meters
         * @return the Builder instance
         */
        Request.Builder addDeadHeadTrip(String originId, String destinationId, int duration, int distance);

        /**
         * Set shunting parameters.
         *
         * @param minimalDuration      the minimal shunting duration in seconds
         * @param deadHeadTripDuration the deadhead trip duration in seconds
         * @param couplingDuration     the coupling trip duration in seconds
         * @return the Builder instance
         */
        Builder setShuntingParameters(int minimalDuration, int deadHeadTripDuration, int couplingDuration);

        /**
         * Set maintenance parameters.
         *
         * @param maximalDistance the maximal distance a vehicle unit can travel without visiting a maintenance slot
         * @return the Builder instance
         */
        Builder setMaintenanceParameters(int maximalDistance);

        /**
         * Set cost parameters.
         *
         * @param staff        the cost per second for the staff on a train formation on a service trip
         * @param serviceTrip  the cost per second for a train formation with k vehicles on a service trip
         * @param maintenance  the cost per second for maintenance (could be negative)
         * @param deadHeadTrip the cost per second for deadhead trips
         * @param idle         the cost per second for idle time
         * @return the Builder instance
         */
        Builder setCostParameters(int staff, int serviceTrip, int maintenance, int deadHeadTrip, int idle);

        /**
         * Set global parameters.
         *
         * @param forbidDeadHeadTrips forbid deadhead trips.
         * @param dayLimitThreshold   vehicles with stopping times under this threshold do not count into dayLimit at
         *                            stations
         * @return the Builder instance
         */
        Builder setGlobalParameters(boolean forbidDeadHeadTrips, int dayLimitThreshold);

        /**
         * Constructs the final Request object based on the added components.
         *
         * @return the Request object
         */
        Request build();
    }
}
