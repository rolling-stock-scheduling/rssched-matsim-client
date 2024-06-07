package ch.sbb.rssched.client;

import ch.sbb.rssched.client.config.RsschedRequestConfig;
import ch.sbb.rssched.client.config.selection.FilterStrategy;
import ch.sbb.rssched.client.config.selection.TransitLineSelection;
import ch.sbb.rssched.client.dto.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static ch.sbb.rssched.client.IntegrationTestData.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RsschedMatsimClientIT {

    private static final String SCHEDULER_BASE_URL = "http://localhost";
    private static final int SCHEDULER_PORT = 3000;

    @BeforeEach
    void setUp() throws IOException {
        new IntegrationTestData(false).setup();
    }

    @Test
    void testProcess() {
        // filter to Kehlheim region
        FilterStrategy filterStrategy = scenario -> {
            TransitLineSelection selection = new TransitLineSelection();
            scenario.getTransitSchedule().getTransitLines().forEach(
                    (transitLineId, transitLine) -> transitLine.getRoutes().forEach((transitRouteId, transitRoute) -> {
                        if (transitRoute.getStops().stream().anyMatch(stop -> MatsimRun.LOCATIONS_IN_KEHLHEIM.contains(
                                stop.getStopFacility().getId().toString()))) {
                            selection.add(TransitLineSelection.NO_GROUP, transitLineId, transitRouteId);
                        }
                    }));
            return selection;
        };

        // build request config
        RsschedRequestConfig.Builder builder = RsschedRequestConfig.builder()
                .setInputDirectory(MatsimRun.INPUT_DIRECTORY).setOutputDirectory(MatsimRun.OUTPUT_DIRECTORY)
                .setRunId(MatsimRun.ID).setFilterStrategy(filterStrategy);

        // add depots
        Depots.LOCATIONS.forEach(location -> {
            String depotId = String.format("dpt_%s", location);
            builder.addDepot(depotId, location, Depots.CAPACITY);
            if (location.startsWith("regio_2")) {
                builder.addAllowedTypeToDepot(depotId, "RE_RB_veh_type", Depots.CAPACITY / 2);
                builder.addAllowedTypeToDepot(depotId, "Bus_veh_type", Depots.CAPACITY / 2);
            } else if (location.startsWith("regio")) {
                builder.addAllowedTypeToDepot(depotId, "RE_RB_veh_type", Depots.CAPACITY);
            } else {
                builder.addAllowedTypeToDepot(depotId, "Bus_veh_type", Depots.CAPACITY);
            }
        });

        // add on route shunting locations
        Shunting.LOCATIONS.forEach(builder::addShuntingLocation);

        // add maintenance day shifts
        MaintenanceSlots.LOCATIONS.forEach(
                location -> builder.addMaintenanceSlot(String.format("mnt_%s_day", location), location,
                        MaintenanceSlots.DAY_SHIFT_START, MaintenanceSlots.DAY_SHIFT_END,
                        MaintenanceSlots.TRACK_COUNT));

        // add maintenance night shifts
        MaintenanceSlots.LOCATIONS.forEach(
                location -> builder.addMaintenanceSlot(String.format("mnt_%s_night", location), location,
                        MaintenanceSlots.NIGHT_SHIFT_START, MaintenanceSlots.NIGHT_SHIFT_END,
                        MaintenanceSlots.TRACK_COUNT));

        // set sample size
        RsschedRequestConfig config = builder.buildWithDefaults();
        config.getGlobal().setSampleSize(MatsimRun.SAMPLE_SIZE);

        // do not create depots at terminal locations
        config.getDepot().setCreateAtTerminalLocations(false);

        // process
        RsschedMatsimClient client = new RsschedMatsimClient(SCHEDULER_BASE_URL, SCHEDULER_PORT);
        Response response = client.process(config);

        assertNotNull(response);
    }
}