package ch.sbb.rssched.client.dto.request;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author munterfi
 */
class RequestBuilderImpl implements Request.Builder {
    private static final int INITIAL_CONFIG_VALUE = -1;
    private final Map<String, VehicleType> vehicleTypes = new HashMap<>();
    private final Map<String, Location> locations = new HashMap<>();
    private final Map<String, Depot> depots = new HashMap<>();
    private final Map<String, Route> routes = new HashMap<>();
    private final Map<String, RouteSegment> routeSegments = new HashMap<>();
    private final Map<String, DepartureSegment> departureSegments = new HashMap<>();
    private final Map<String, Departure> departures = new HashMap<>();
    private final Map<String, MaintenanceSlot> maintenanceSlots = new HashMap<>();
    private final TripMatrix.Builder tripMatrixBuilder = new TripMatrix.Builder();
    private ShuntingConfig shunting;
    private CostConfig costs;
    private MaintenanceConfig maintenance;
    private boolean forbidDeadHeadTrips;
    private int dayLimitThreshold = INITIAL_CONFIG_VALUE;


    RequestBuilderImpl() {
    }

    @Override
    public Request.Builder addLocation(String id) {
        if (locations.containsKey(id)) {
            throw new IllegalArgumentException("Location with ID " + id + " already exists.");
        }
        Location location = new Location(id);
        locations.put(id, location);
        return this;
    }

    @Override
    public Request.Builder addLocation(String id, int dayLimit) {
        if (locations.containsKey(id)) {
            throw new IllegalArgumentException("Location with ID " + id + " already exists.");
        }
        Location location = new Location(id, dayLimit);
        locations.put(id, location);
        return this;
    }

    @Override
    public Request.Builder addVehicleType(String id, int capacity, int seats, int maximalFormationCount) {
        if (vehicleTypes.containsKey(id)) {
            throw new IllegalArgumentException("VehicleType with ID " + id + " already exists.");
        }
        VehicleType vehicleType = new VehicleType(id, capacity, seats, maximalFormationCount);
        vehicleTypes.put(id, vehicleType);
        return this;
    }

    @Override
    public Request.Builder addDepot(String id, String locationId, int capacity) {
        if (depots.containsKey(id)) {
            throw new IllegalArgumentException("Depot with ID " + id + " already exists.");
        }
        if (!locations.containsKey(locationId)) {
            throw new IllegalArgumentException("Location " + locationId + " does not exist.");
        }
        depots.put(id, new Depot(id, locationId, capacity));
        return this;
    }

    @Override
    public Request.Builder addVehicleTypeToDepot(String depotId, String vehicleTypeId, int upperBound) {
        Depot depot = depots.get(depotId);
        if (depot == null) {
            throw new IllegalArgumentException("Depot with ID " + depotId + " does not exist.");
        }
        if (!vehicleTypes.containsKey(vehicleTypeId)) {
            throw new IllegalArgumentException("Vehicle type " + vehicleTypeId + " does not exist.");
        }
        depot.addUpperBoundForVehicleType(vehicleTypeId, upperBound);
        return this;
    }

    @Override
    public Request.Builder addRoute(String id, String vehicleTypeId) {
        if (routes.containsKey(id)) {
            throw new IllegalArgumentException("Route with ID " + id + " already exists.");
        }
        if (!vehicleTypes.containsKey(vehicleTypeId)) {
            throw new IllegalArgumentException("Vehicle type " + vehicleTypeId + " does not exist.");
        }
        routes.put(id, new Route(id, vehicleTypeId));
        return this;
    }

    @Override
    public Request.Builder addSegmentToRoute(String id, String routeId, String originId, String destinationId, int distance, int duration, int maximalFormationCount) {
        if (routeSegments.containsKey(id)) {
            throw new IllegalArgumentException("Route segment with ID " + id + " already exists.");
        }
        if (!locations.containsKey(originId)) {
            throw new IllegalArgumentException("Origin " + originId + " does not exist.");
        }
        if (!locations.containsKey(destinationId)) {
            throw new IllegalArgumentException("Destination " + destinationId + " does not exist.");
        }
        Route route = routes.get(routeId);
        if (route == null) {
            throw new IllegalArgumentException("Route with ID " + routeId + " does not exist.");
        }
        routeSegments.put(id, route.addSegment(id, originId, destinationId, distance, duration, maximalFormationCount));
        return this;
    }

    @Override
    public Request.Builder addDeparture(String id, String routeId) {
        if (departures.containsKey(id)) {
            throw new IllegalArgumentException("Departure trip with ID " + id + " already exists.");
        }
        if (!routes.containsKey(routeId)) {
            throw new IllegalArgumentException("Route with ID " + routeId + " does not exist.");
        }
        departures.put(id, new Departure(id, routeId));
        return this;
    }

    @Override
    public Request.Builder addSegmentToDeparture(String id, String departureId, String routeSegmentId, LocalDateTime departureTime, int passengers, int seats) {
        if (departureSegments.containsKey(id)) {
            throw new IllegalArgumentException("Departure segment with ID " + id + " already exists.");
        }
        Departure departure = departures.get(departureId);
        if (departure == null) {
            throw new IllegalArgumentException("Departure with ID " + departureId + " does not exist.");
        }
        RouteSegment routeSegment = routeSegments.get(routeSegmentId);
        if (routeSegment == null) {
            throw new IllegalArgumentException("Route segment with ID " + routeSegmentId + " does not exist.");
        }
        // ensure route segment is from route on departure
        Route route = routes.get(departure.getRoute());
        if (!route.getSegments().get(routeSegment.getOrder()).getId().equals(routeSegment.getId())) {
            throw new IllegalArgumentException(String.format(
                    "Route segment with ID %s does not belong to the route %s assigned to the departure %s.",
                    routeSegmentId, route.getId(), departureId));
        }
        // ensure a route segment can only be covered once per departure
        if (departure.getSegments().stream().anyMatch(seg -> seg.routeSegment().equals(routeSegmentId))) {
            throw new IllegalArgumentException(
                    "Duplicate route segment " + routeSegmentId + " in departure " + departureId);
        }
        // ensure departure time is not before arrival time of previous segment
        if (routeSegment.getOrder() > 0) {
            RouteSegment previousRouteSegment = route.getSegments().get(routeSegment.getOrder() - 1);
            DepartureSegment previousDepartureSegment = departure.getSegments().get(routeSegment.getOrder() - 1);
            LocalDateTime lastArrivalTime = previousDepartureSegment.departure()
                    .plusSeconds(previousRouteSegment.getDuration());
            if (departureTime.isBefore(lastArrivalTime)) {
                throw new IllegalArgumentException(String.format(
                        "Departure time %s on departure segment %s (route segment: %s) is before last arrival time %s on departure segment %s (route segment: %s)",
                        departureTime, id, routeSegmentId, lastArrivalTime, previousDepartureSegment.id(),
                        previousRouteSegment.getId()));
            }
        }
        // valid, create and add departure segment
        departureSegments.put(id,
                departure.addSegment(id, routeSegmentId, departureTime.withNano(0), passengers, seats));
        return this;
    }

    @Override
    public Request.Builder addMaintenanceSlot(String id, String locationId, LocalDateTime start, LocalDateTime end) {
        if (maintenanceSlots.containsKey(id)) {
            throw new IllegalArgumentException("Maintenance slot with ID " + id + " already exists.");
        }
        if (!locations.containsKey(locationId)) {
            throw new IllegalArgumentException("Location " + locationId + " does not exist.");
        }
        maintenanceSlots.put(id, new MaintenanceSlot(id, locationId, start.withNano(0), end.withNano(0)));
        return this;
    }

    @Override
    public Request.Builder addDeadHeadTrip(String originId, String destinationId, int duration, int distance) {
        if (!locations.containsKey(originId)) {
            throw new IllegalArgumentException("Origin location with ID " + originId + " does not exist.");
        }
        if (!locations.containsKey(destinationId)) {
            throw new IllegalArgumentException("Destination location with ID " + destinationId + " does not exist.");
        }
        tripMatrixBuilder.addRelation(originId, destinationId, duration, distance);
        return this;
    }


    @Override
    public Request.Builder setShuntingParameters(int minimalDuration, int deadHeadTripDuration, int couplingDuration) {
        this.shunting = new ShuntingConfig(minimalDuration, deadHeadTripDuration, couplingDuration);
        return this;
    }

    @Override
    public Request.Builder setMaintenanceParameters(int maximalDistance) {
        this.maintenance = new MaintenanceConfig(maximalDistance);
        return this;
    }

    @Override
    public Request.Builder setCostParameters(int staff, int serviceTrip, int maintenance, int deadHeadTrip, int idle) {
        this.costs = new CostConfig(staff, serviceTrip, maintenance, deadHeadTrip, idle);
        return this;
    }

    @Override
    public Request.Builder setGlobalParameters(boolean forbidDeadHeadTrips, int dayLimitThreshold) {
        this.forbidDeadHeadTrips = forbidDeadHeadTrips;
        this.dayLimitThreshold = dayLimitThreshold;
        return this;
    }

    @Override
    public Request build() {
        validate();
        RequestImpl request = new RequestImpl();
        request.setVehicleTypes(getSortedValues(vehicleTypes));
        request.setLocations(getSortedValues(locations));
        request.setDepots(getSortedValues(depots));
        request.setRoutes(getSortedValues(routes));
        request.setDepartures(getSortedValues(departures));
        request.setMaintenanceSlots(getSortedValues(maintenanceSlots));
        request.setDeadHeadTrips(tripMatrixBuilder.build());
        request.setParameters(new Config(forbidDeadHeadTrips, dayLimitThreshold, shunting, maintenance, costs));
        return request;
    }

    private void validate() {
        checkInputs();
        checkParameters();
        checkDepots();
        checkVehicleType();
        checkTripMatrixIndices();
    }

    private void checkInputs() {
        if (vehicleTypes.isEmpty()) {
            throw new IllegalStateException("VehicleTypes are empty. Add at least one VehicleType.");
        }
        if (locations.isEmpty()) {
            throw new IllegalStateException("Locations are empty. Add at least one Location.");
        }
        if (depots.isEmpty()) {
            throw new IllegalStateException("Depots are empty. Add at least one Depot.");
        }
        if (routes.isEmpty()) {
            throw new IllegalStateException("Routes are empty. Add at least one Route.");
        }
        if (departures.isEmpty()) {
            throw new IllegalStateException("Departures are empty. Add at least one Departures.");
        }
    }

    private void checkParameters() {
        if (shunting == null || maintenance == null || costs == null) {
            throw new IllegalStateException("Parameters (shunting, maintenance or costs) are null.");
        }
        if (this.dayLimitThreshold == INITIAL_CONFIG_VALUE) {
            throw new IllegalStateException("Parameters forbidDeadHeadTrips and dayLimitThreshold not set.");
        }
    }

    private void checkDepots() {
        for (Depot depot : depots.values()) {
            if (depot.hasNoVehicleTypesAssigned()) {
                throw new IllegalStateException("Depot with ID " + depot.getId() + " has no vehicle types assigned.");
            }
        }
    }

    private void checkVehicleType() {
        for (VehicleType vehicleType : vehicleTypes.values()) {
            if (!hasAtLeastOneDepotAssigned(vehicleType.id())) {
                throw new IllegalStateException("VehicleType with ID " + vehicleType.id() + " has no depots assigned.");
            }
        }
    }

    private boolean hasAtLeastOneDepotAssigned(String vehicleTypeId) {
        for (Depot depot : depots.values()) {
            if (depot.supportsVehicleType(vehicleTypeId)) {
                return true;
            }
        }
        return false;
    }

    private void checkTripMatrixIndices() {
        for (String originId : locations.keySet()) {
            for (String destinationId : locations.keySet()) {
                if (originId.equals(destinationId)) {
                    continue;
                }
                if (!tripMatrixBuilder.containsRelation(originId, destinationId)) {
                    throw new IllegalStateException(
                            "Trip matrix is missing relation from " + originId + " to " + destinationId);
                }
            }
        }
    }

    private <T extends Comparable<T>> List<T> getSortedValues(Map<String, T> map) {
        List<T> sortedList = new ArrayList<>(map.values());
        Collections.sort(sortedList);
        return sortedList;
    }
}
