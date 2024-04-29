package ch.sbb.rssched.client;

import ch.sbb.rssched.client.config.RsschedRequestConfig;
import ch.sbb.rssched.client.config.selection.FilterStrategy;
import ch.sbb.rssched.client.config.selection.TransitLineSelection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Set;

import static ch.sbb.rssched.client.IntegrationTestData.*;

class RsschedMatsimClientIT {

    public static final LocalDateTime DAY_SHIFT_START = LocalDateTime.of(2020, 1, 1, 8, 0, 0);
    public static final LocalDateTime DAY_SHIFT_END = LocalDateTime.of(2020, 1, 1, 16, 0, 0);
    public static final LocalDateTime NIGHT_SHIFT_START = LocalDateTime.of(2020, 1, 1, 20, 0, 0);
    public static final LocalDateTime NIGHT_SHIFT_END = LocalDateTime.of(2020, 1, 2, 4, 0, 0);
    public static final int DEPOT_CAPACITY = 10;
    public static final int TRACK_COUNT = 2;
    private final Set<String> STOPS_IN_KEHLHEIM = Set.of("short_10302.5", "regio_275062", "short_10302.6",
            "regio_289114.2", "regio_289114.4", "regio_92976", "short_10302.3", "short_10302.4", "regio_275062.1",
            "short_9488", "short_10302.1", "short_10302.2", "short_9488.1", "short_9488.4", "short_9488.5",
            "short_9488.2", "short_9488.3", "short_9488.6", "short_10302", "short_12323", "regio_289114",
            "regio_289114.1", "regio_289114.3", "regio_185728");
    private final Set<String> DEPOT_MAINTENANCE_LOCATIONS = Set.of("regio_141134", "regio_151384", "regio_172317",
            "regio_215502", "short_1119", "short_3740", "short_5972", "short_6900");


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
                    (transitLineId, transitLine) -> transitLine.getRoutes()
                            .forEach((transitRouteId, transitRoute) -> {
                                if (transitRoute.getStops().stream().anyMatch(stop -> STOPS_IN_KEHLHEIM.contains(
                                        stop.getStopFacility().getId().toString()))) {
                                    selection.add(TransitLineSelection.NO_GROUP, transitLineId, transitRouteId);
                                }
                            }));
            return selection;
        };

        // build request config
        RsschedRequestConfig.Builder builder = RsschedRequestConfig.builder().setInputDirectory(IT_INPUT_DIRECTORY)
                .setOutputDirectory(IT_OUTPUT_DIRECTORY).setRunId(RUN_ID).setFilterStrategy(filterStrategy);

        // add depots
        DEPOT_MAINTENANCE_LOCATIONS.forEach(location -> {
            String depotId = String.format("dpt_%s", location);
            builder.addDepot(depotId, location, DEPOT_CAPACITY);
            if (location.startsWith("regio")) {
                builder.addAllowedTypeToDepot(depotId, "RE_RB_veh_type", DEPOT_CAPACITY);
            } else {
                builder.addAllowedTypeToDepot(depotId, "Bus_veh_type", DEPOT_CAPACITY);
            }
        });

        // add maintenance day shifts
        DEPOT_MAINTENANCE_LOCATIONS.forEach(
                location -> builder.addMaintenanceSlot(String.format("mnt_%s_day", location), location, DAY_SHIFT_START,
                        DAY_SHIFT_END, TRACK_COUNT));

        // add maintenance night shifts
        DEPOT_MAINTENANCE_LOCATIONS.forEach(
                location -> builder.addMaintenanceSlot(String.format("mnt_%s_night", location), location,
                        NIGHT_SHIFT_START, NIGHT_SHIFT_END, TRACK_COUNT));

        // set sample size
        RsschedRequestConfig config = builder.buildWithDefaults();
        config.getGlobal().setSampleSize(0.01);

        // do not create depots at terminal locations
        config.getDepot().setCreateAtTerminalLocations(false);

        // process
        RsschedMatsimClient client = new RsschedMatsimClient("localhost", 3000);
        client.process(config);
    }
}