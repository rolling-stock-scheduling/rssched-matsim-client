package ch.sbb.rssched.client.dto.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

class RequestTest {

    private final static LocalDateTime DAY_START = LocalDateTime.of(2020, 1, 1, 6, 0, 0);
    private final static LocalDateTime MAINTENANCE_DAY_START = LocalDateTime.of(2020, 1, 1, 8, 0, 0);
    private final static LocalDateTime MAINTENANCE_DAY_END = LocalDateTime.of(2020, 1, 1, 16, 0, 0);
    private final static LocalDateTime MAINTENANCE_NIGHT_START = LocalDateTime.of(2020, 1, 1, 20, 0, 0);
    private final static LocalDateTime MAINTENANCE_NIGHT_END = LocalDateTime.of(2020, 1, 2, 4, 0, 0);

    private Request.Builder requestBuilder;

    @BeforeEach
    void setUp() {
        requestBuilder = Request.builder();
    }

    @Test
    void testBuildWithValidData() throws JsonProcessingException {
        Request request = Request.builder()
                .addLocation("loc1")
                .addLocation("loc2", 5)
                .addLocation("loc3")
                .addVehicleType("vt1", 100, 60, 2)
                .addVehicleType("vt2", 80, 50, 3)
                .addDepot("depot1", "loc1", 30)
                .addDepot("depot2", "loc2", 50)
                .addVehicleTypeToDepot("depot1", "vt2", 5)
                .addVehicleTypeToDepot("depot1", "vt1", 7)
                .addVehicleTypeToDepot("depot2", "vt2", 8)
                .addRoute("route1", "vt1")
                .addRoute("route2", "vt2")
                .addSegmentToRoute("routeSegment1", "route1", "loc1", "loc3", 1000, 3600, 3)
                .addSegmentToRoute("routeSegment2", "route2", "loc1", "loc2", 400, 1200, 2)
                .addSegmentToRoute("routeSegment3", "route2", "loc2", "loc3", 600, 2400, 3)
                .addDeparture("departure1", "route1")
                .addDeparture("departure2", "route1")
                .addDeparture("departure3", "route1")
                .addDeparture("departure4", "route2")
                .addDeparture("departure5", "route2")
                .addDeparture("departure6", "route2")
                .addSegmentToDeparture("departureSegment1", "departure1", "routeSegment1", DAY_START.plusMinutes(10),
                        40, 40)
                .addSegmentToDeparture("departureSegment2", "departure2", "routeSegment1", DAY_START.plusMinutes(20),
                        150, 110)
                .addSegmentToDeparture("departureSegment3", "departure3", "routeSegment1", DAY_START.plusMinutes(30),
                        80, 60)
                .addSegmentToDeparture("departureSegment4", "departure4", "routeSegment2", DAY_START.plusMinutes(5),
                        100, 80)
                .addSegmentToDeparture("departureSegment5", "departure5", "routeSegment2", DAY_START.plusMinutes(15),
                        110, 90)
                .addSegmentToDeparture("departureSegment6", "departure6", "routeSegment2", DAY_START.plusMinutes(25),
                        90, 70)
                .addSegmentToDeparture("departureSegment7", "departure4", "routeSegment3",
                        DAY_START.plusMinutes(5).plusSeconds(1380), 110, 80)
                .addSegmentToDeparture("departureSegment8", "departure5", "routeSegment3",
                        DAY_START.plusMinutes(15).plusSeconds(1380), 160, 130)
                .addSegmentToDeparture("departureSegment9", "departure6", "routeSegment3",
                        DAY_START.plusMinutes(25).plusSeconds(1380), 70, 70)
                .addMaintenanceSlot("maintenance1", "loc2", MAINTENANCE_DAY_START,
                        MAINTENANCE_DAY_END)
                .addMaintenanceSlot("maintenance2", "loc2", MAINTENANCE_DAY_START,
                        MAINTENANCE_DAY_END)
                .addMaintenanceSlot("maintenance3", "loc2", MAINTENANCE_NIGHT_START,
                        MAINTENANCE_NIGHT_END)
                .addDeadHeadTrip("loc1", "loc2", 600, 1000)
                .addDeadHeadTrip("loc2", "loc1", 6000, 10000)
                .addDeadHeadTrip("loc1", "loc3", 300, 500)
                .addDeadHeadTrip("loc3", "loc1", 3000, 5000)
                .addDeadHeadTrip("loc2", "loc3", 400, 700)
                .addDeadHeadTrip("loc3", "loc2", 4000, 7000)
                .setShuntingParameters(10 * 60, 5 * 60, 5 * 60)
                .setMaintenanceParameters(6000)
                .setCostParameters(5, 7, -10, 10, 2)
                .setGlobalParameters(false, 5 * 60)
                .build();

        String jsonOutput = request.toJSON();
        System.out.println(jsonOutput);

        Assertions.assertFalse(jsonOutput.isEmpty(), "JSON output should not be empty.");
        Assertions.assertTrue(jsonOutput.contains("loc1"), "JSON should contain 'loc1'.");
        Assertions.assertTrue(jsonOutput.contains("vt1"), "JSON should contain 'vt1'.");
        Assertions.assertTrue(jsonOutput.contains("route1"), "JSON should contain 'route1'.");
        Assertions.assertTrue(jsonOutput.contains("routeSegment1"), "JSON should contain 'routeSegment1'.");
        Assertions.assertTrue(jsonOutput.contains("departureSegment1"), "JSON should contain 'departureSegment1'.");
    }

    @Test
    void testBuildWithMissingData() {
        Assertions.assertThrows(IllegalStateException.class, () -> requestBuilder.build());
    }

    @Test
    void testBuildWithMissingDepotVehicleType() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> requestBuilder
                .addLocation("loc1")
                .addVehicleType("vt1", 50, 100, 10)
                .addDepot("depot1", "loc1", 30)
                .addRoute("route2", "vt2"));
    }

}