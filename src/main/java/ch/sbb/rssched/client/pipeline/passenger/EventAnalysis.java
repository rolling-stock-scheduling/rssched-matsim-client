package ch.sbb.rssched.client.pipeline.passenger;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.events.PersonEntersVehicleEvent;
import org.matsim.api.core.v01.events.PersonLeavesVehicleEvent;
import org.matsim.api.core.v01.events.TransitDriverStartsEvent;
import org.matsim.api.core.v01.events.handler.PersonEntersVehicleEventHandler;
import org.matsim.api.core.v01.events.handler.PersonLeavesVehicleEventHandler;
import org.matsim.api.core.v01.events.handler.TransitDriverStartsEventHandler;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.api.experimental.events.VehicleArrivesAtFacilityEvent;
import org.matsim.core.api.experimental.events.VehicleDepartsAtFacilityEvent;
import org.matsim.core.api.experimental.events.handler.VehicleArrivesAtFacilityEventHandler;
import org.matsim.core.api.experimental.events.handler.VehicleDepartsAtFacilityEventHandler;
import org.matsim.core.utils.misc.Time;
import org.matsim.pt.transitSchedule.api.Departure;
import org.matsim.pt.transitSchedule.api.TransitLine;
import org.matsim.pt.transitSchedule.api.TransitRoute;
import org.matsim.pt.transitSchedule.api.TransitRouteStop;
import org.matsim.pt.transitSchedule.api.TransitStopFacility;
import org.matsim.vehicles.Vehicle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Event analysis to count access, egress and total passenger of a simulation run.
 * <p>
 * All transit vehicle that are travelling on a transit line of interest, are attached with a tracker, which processes
 * vehicle and passenger-related events in a simulation run. It maintains a list of entries that capture information
 * about the passenger counts, access counts and egress counts at every departure at a transit stop facility.
 *
 * @author munterfi
 */
@Log4j2
public class EventAnalysis implements TransitDriverStartsEventHandler, VehicleDepartsAtFacilityEventHandler, VehicleArrivesAtFacilityEventHandler, PersonEntersVehicleEventHandler, PersonLeavesVehicleEventHandler {
    @Getter
    private final List<Entry> entries = new ArrayList<>(1000);
    private final Map<Id<Vehicle>, TransitVehicleTracker> trackers = new HashMap<>();
    private final Map<Id<Person>, Access> passengerAccessTimes = new HashMap<>();
    private final Set<Id<Vehicle>> activeTransitVehicles = new HashSet<>();
    private final Scenario scenario;
    private final Set<Id<TransitLine>> transitLineIds;
    private final int seatDurationThreshold;
    private final double sampleSizeFactor;

    /**
     * Constructs a PassengerEventAnalysis object with the specified scenario and transit line IDs.
     *
     * @param scenario       The scenario representing the simulation run.
     * @param transitLineIds The set of transit line IDs to consider for passenger analysis.
     */
    EventAnalysis(Scenario scenario, Set<Id<TransitLine>> transitLineIds, double sampleSizeFactor, int seatDurationThreshold) {
        this.scenario = scenario;
        this.transitLineIds = transitLineIds;
        this.seatDurationThreshold = seatDurationThreshold;
        this.sampleSizeFactor = sampleSizeFactor;
    }

    @Override
    public void handleEvent(TransitDriverStartsEvent event) {
        var vehicleId = event.getVehicleId();
        var vehicle = scenario.getTransitVehicles().getVehicles().get(vehicleId);
        var transitLineId = event.getTransitLineId();
        if (vehicle != null && transitLineIds.contains(transitLineId)) {
            var transitLine = scenario.getTransitSchedule().getTransitLines().get(transitLineId);
            var transitRoute = transitLine.getRoutes().get(event.getTransitRouteId());
            var tracker = this.trackers.computeIfAbsent(vehicleId, k -> new TransitVehicleTracker(vehicle));
            var departure = transitRoute.getDepartures().get(event.getDepartureId());
            tracker.registerRouteDeparture(event.getDriverId(), transitLine, transitRoute, departure);
            log.debug("Registered new departure of transit vehicle '{}' at {} (totalActive: {}, trackers: {})",
                    vehicle.getId(), Time.writeTime(event.getTime(), Time.TIMEFORMAT_HHMMSS),
                    activeTransitVehicles.size(), trackers.size());
        }
    }

    @Override
    public void handleEvent(VehicleArrivesAtFacilityEvent event) {
        var vehicleId = event.getVehicleId();
        if (activeTransitVehicles.contains(vehicleId)) {
            var transitStopFacility = scenario.getTransitSchedule().getFacilities().get(event.getFacilityId());
            trackers.get(vehicleId).registerStopArrival(transitStopFacility);
        }
    }

    @Override
    public void handleEvent(VehicleDepartsAtFacilityEvent event) {
        var vehicleId = event.getVehicleId();
        if (activeTransitVehicles.contains(vehicleId)) {
            trackers.get(vehicleId).registerStopDeparture();
        }
    }

    @Override
    public void handleEvent(PersonEntersVehicleEvent event) {
        var vehicleId = event.getVehicleId();
        if (activeTransitVehicles.contains(vehicleId)) {
            trackers.get(vehicleId).registerAccess(event.getPersonId(), event.getTime());
        }
    }

    @Override
    public void handleEvent(PersonLeavesVehicleEvent event) {
        var vehicleId = event.getVehicleId();
        if (activeTransitVehicles.contains(vehicleId)) {
            trackers.get(vehicleId).registerEgress(event.getPersonId(), event.getTime());
        }
    }

    private void notifyTransitVehicleDeactivation(Id<Vehicle> vehicleId) {
        activeTransitVehicles.remove(vehicleId);
    }

    private void notifyTransitVehicleActivation(Id<Vehicle> vehicleId) {
        activeTransitVehicles.add(vehicleId);
    }

    private void notifyNewRecordEntry(Entry entry) {
        entries.add(entry);
    }

    record Access(double time, int index) {
    }

    /**
     * An entry in the passenger event analysis.
     * <p>
     * This class captures information from a vehicle tracker when it arrives at a stop.
     */
    @Getter
    public static final class Entry {
        private final TransitLine transitLine;
        private final TransitRoute transitRoute;
        private final Departure departure;
        private final TransitRouteStop fromStop;
        private final TransitRouteStop toStop;
        private final int egress;
        private final int access;
        private final int count;
        private int seats;

        /**
         * @param transitLine  The transit line associated with the tracker.
         * @param transitRoute The transit route currently taken by the vehicle.
         * @param departure    The departure time from the previous stop.
         * @param fromStop     The current transit route stop.
         * @param toStop       The next transit route stop.
         * @param egress       The number of egresses at the current stop.
         * @param access       The number of accesses at the current stop.
         * @param count        The count of passengers between the current and the next stop.
         */
        public Entry(TransitLine transitLine, TransitRoute transitRoute, Departure departure, TransitRouteStop fromStop, TransitRouteStop toStop, int egress, int access, int count) {
            this.transitLine = transitLine;
            this.transitRoute = transitRoute;
            this.departure = departure;
            this.fromStop = fromStop;
            this.toStop = toStop;
            this.egress = egress;
            this.access = access;
            this.count = count;
            this.seats = 0;
        }

    }

    /**
     * Track transit vehicle through the event stream.
     */
    class TransitVehicleTracker {
        private final Vehicle vehicle;
        private final List<Entry> entries = new ArrayList<>();
        private Id<Person> currentDriverId;
        private TransitLine currentLine;
        private TransitRoute currentRoute;
        private Departure currentDeparture;
        private int stopIdx;
        private int passenger;
        private int access;
        private int egress;
        private boolean atTerminalStop;

        TransitVehicleTracker(Vehicle vehicle) {
            this.vehicle = vehicle;
            passenger = 0;
            access = 0;
            egress = 0;
        }

        void registerRouteDeparture(Id<Person> driverId, TransitLine transitLine, TransitRoute transitRoute, Departure departure) {
            assert passenger == 0 : "Passenger count is not 0, new departure not possible";
            currentDriverId = driverId;
            currentLine = transitLine;
            currentRoute = transitRoute;
            currentDeparture = departure;
            stopIdx = -1; // first arrival is at first stop of route, therefore start at -1
            atTerminalStop = false;
            notifyTransitVehicleActivation(vehicle.getId());
        }

        void registerStopArrival(TransitStopFacility transitStopFacility) {
            assert currentRoute.getStops().get(stopIdx + 1).getStopFacility().getId()
                    .equals(transitStopFacility.getId()) : "Stop facility id not matching stop index position";
            stopIdx++;
            access = 0;
            egress = 0;
            if (stopIdx == currentRoute.getStops().size() - 1) {
                atTerminalStop = true;
            }
        }

        void registerStopDeparture() {
            var currentStop = currentRoute.getStops().get(stopIdx);
            var nextStop = atTerminalStop ? null : currentRoute.getStops().get(stopIdx + 1);
            int currentEgress = (int) Math.round(egress * sampleSizeFactor);
            int currentAccess = (int) Math.round(access * sampleSizeFactor);
            int currentCount = (int) Math.round(passenger * sampleSizeFactor);
            var entry = new Entry(currentLine, currentRoute, currentDeparture, currentStop, nextStop, currentEgress,
                    currentAccess, currentCount);
            entries.add(entry);
            notifyNewRecordEntry(entry);
        }

        void registerAccess(Id<Person> personId, double time) {
            if (!currentDriverId.equals(personId)) {
                access++;
                passenger++;
                passengerAccessTimes.put(personId, new Access(time, entries.size()));
            }
        }

        void registerEgress(Id<Person> personId, double time) {
            if (currentDriverId.equals(personId)) {
                assert atTerminalStop : "Cannot complete route without being at terminal stop.";
                notifyTransitVehicleDeactivation(vehicle.getId());
            } else {
                egress++;
                passenger--;
                Access access = passengerAccessTimes.remove(personId);
                if (time - access.time > seatDurationThreshold) {
                    for (int i = access.index; i < entries.size(); i++) {
                        entries.get(i).seats += (int) (1 * sampleSizeFactor);
                    }
                }

            }
        }
    }
}

