package ch.sbb.rssched.client;

import ch.sbb.rssched.client.config.RsschedRequestConfig;
import ch.sbb.rssched.client.config.selection.FilterStrategy;
import ch.sbb.rssched.client.config.selection.TransitLineSelection;
import ch.sbb.rssched.client.dto.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

import static ch.sbb.rssched.client.IntegrationTestData.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RsschedMatsimClientIT {

    public static final LocalDateTime DAY_SHIFT_START = LocalDateTime.of(LocalDate.now(), LocalTime.of(8, 0, 0));
    public static final LocalDateTime DAY_SHIFT_END = LocalDateTime.of(LocalDate.now(), LocalTime.of(16, 0, 0));
    public static final LocalDateTime NIGHT_SHIFT_START = LocalDateTime.of(LocalDate.now(), LocalTime.of(20, 0, 0));
    public static final LocalDateTime NIGHT_SHIFT_END = LocalDateTime.of(LocalDate.now().plusDays(1),
            LocalTime.of(4, 0, 0));
    public static final int DEPOT_CAPACITY = 10;
    public static final int MAINTENANCE_TRACK_COUNT = 3;
    private final Set<String> STOPS_IN_KEHLHEIM = Set.of("short_10302.5", "regio_275062", "short_10302.6",
            "regio_289114.2", "regio_289114.4", "regio_92976", "short_10302.3", "short_10302.4", "regio_275062.1",
            "short_9488", "short_10302.1", "short_10302.2", "short_9488.1", "short_9488.4", "short_9488.5",
            "short_9488.2", "short_9488.3", "short_9488.6", "short_10302", "short_12323", "regio_289114",
            "regio_289114.1", "regio_289114.3", "regio_185728");
    private final Set<String> ON_ROUTE_SHUNTING_LOCATIONS = Set.of("regio_141134", "regio_166759", "regio_314854",
            "regio_215502", "short_1119", "short_6474", "short_5972", "short_9804");
    private final Set<String> DEPOT_LOCATIONS = Set.of("regio_141134", "regio_145538", "regio_147682", "regio_151384",
            "regio_172317", "regio_215502", "regio_293170", "short_1119", "short_3740", "short_5972", "short_6900");
    private final Set<String> MAINTENANCE_LOCATIONS = Set.of("regio_67809", "regio_81147", "regio_293170", "short_9666",
            "short_9804", "short_1119");

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
                        if (transitRoute.getStops().stream().anyMatch(
                                stop -> STOPS_IN_KEHLHEIM.contains(stop.getStopFacility().getId().toString()))) {
                            selection.add(TransitLineSelection.NO_GROUP, transitLineId, transitRouteId);
                        }
                    }));
            return selection;
        };

        // build request config
        RsschedRequestConfig.Builder builder = RsschedRequestConfig.builder().setInputDirectory(IT_INPUT_DIRECTORY)
                .setOutputDirectory(IT_OUTPUT_DIRECTORY).setRunId(RUN_ID).setFilterStrategy(filterStrategy);

        // add depots
        DEPOT_LOCATIONS.forEach(location -> {
            String depotId = String.format("dpt_%s", location);
            builder.addDepot(depotId, location, DEPOT_CAPACITY);
            if (location.startsWith("regio_2")) {
                builder.addAllowedTypeToDepot(depotId, "RE_RB_veh_type", DEPOT_CAPACITY / 2);
                builder.addAllowedTypeToDepot(depotId, "Bus_veh_type", DEPOT_CAPACITY / 2);
            } else if (location.startsWith("regio")) {
                builder.addAllowedTypeToDepot(depotId, "RE_RB_veh_type", DEPOT_CAPACITY);
            } else {
                builder.addAllowedTypeToDepot(depotId, "Bus_veh_type", DEPOT_CAPACITY);
            }
        });

        // add on route shunting locations
        ON_ROUTE_SHUNTING_LOCATIONS.forEach(builder::addShuntingLocation);

        // add maintenance day shifts
        MAINTENANCE_LOCATIONS.forEach(
                location -> builder.addMaintenanceSlot(String.format("mnt_%s_day", location), location, DAY_SHIFT_START,
                        DAY_SHIFT_END, MAINTENANCE_TRACK_COUNT));

        // add maintenance night shifts
        MAINTENANCE_LOCATIONS.forEach(
                location -> builder.addMaintenanceSlot(String.format("mnt_%s_night", location), location,
                        NIGHT_SHIFT_START, NIGHT_SHIFT_END, MAINTENANCE_TRACK_COUNT));

        // set sample size
        RsschedRequestConfig config = builder.buildWithDefaults();
        config.getGlobal().setSampleSize(0.25);

        // do not create depots at terminal locations
        config.getDepot().setCreateAtTerminalLocations(false);

        // process
        RsschedMatsimClient client = new RsschedMatsimClient("http://localhost", 3000);
        Response response = client.process(config);

        assertNotNull(response);
    }
}