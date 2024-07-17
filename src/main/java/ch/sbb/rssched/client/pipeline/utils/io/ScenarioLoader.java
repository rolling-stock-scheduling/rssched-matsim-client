package ch.sbb.rssched.client.pipeline.utils.io;

import lombok.extern.log4j.Log4j2;
import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.scenario.ScenarioUtils;

@Log4j2
public class ScenarioLoader {
    private static final String NETWORK_FILE = "output_network.xml.gz";
    private static final String TRANSIT_SCHEDULE_FILE = "output_transitSchedule.xml.gz";
    private static final String TRANSIT_VEHICLES_FILE = "output_transitVehicles.xml.gz";
    private static final String EVENTS_FILE = "output_events.xml.gz";
    private final String runId;
    private final String inputFolder;
    private final String networkCrs;

    /**
     * Constructs a ScenarioLoader object with the specified run ID, input folder and the CRS.
     * <p>
     * Note: If a CRS is provided the network will be read.
     *
     * @param runId       the ID of the simulation run
     * @param inputFolder the folder containing the output files of the run
     * @param networkCrs  the coordinate reference system of the network
     */
    public ScenarioLoader(String runId, String inputFolder, String networkCrs) {
        this.runId = runId;
        this.inputFolder = inputFolder;
        this.networkCrs = networkCrs;
    }

    /**
     * A scenario loader created with this constructor will not load the network.
     */
    public ScenarioLoader(String runId, String inputFolder) {
        // crs is only needed if network is loaded
        this(runId, inputFolder, null);
    }

    public Scenario load() {
        log.info("Loading scenario {}", runId);
        String networkFile = networkCrs != null ? buildRelativeFileName(NETWORK_FILE) : null;
        String scheduleFile = buildRelativeFileName(TRANSIT_SCHEDULE_FILE);
        String vehiclesFile = buildRelativeFileName(TRANSIT_VEHICLES_FILE);
        Config config = ConfigUtils.createConfig(inputFolder);
        config.global().setCoordinateSystem(networkCrs);
        config.plans().setInputFile(null);
        config.facilities().setInputFile(null);
        config.vehicles().setVehiclesFile(null);
        config.network().setInputFile(networkFile);
        config.transit().setUseTransit(true);
        config.transit().setTransitScheduleFile(scheduleFile);
        config.transit().setVehiclesFile(vehiclesFile);
        return ScenarioUtils.loadScenario(config);
    }

    public String getEventsFile() {
        return buildPath(EVENTS_FILE);
    }

    private String buildPath(String fileType) {
        if (inputFolder.endsWith("/")) {
            return String.format("%s%s.%s", inputFolder, runId, fileType);
        } else {
            return String.format("%s/%s.%s", inputFolder, runId, fileType);
        }
    }

    private String buildRelativeFileName(String fileType) {
        return String.format("%s.%s", runId, fileType);
    }

}
