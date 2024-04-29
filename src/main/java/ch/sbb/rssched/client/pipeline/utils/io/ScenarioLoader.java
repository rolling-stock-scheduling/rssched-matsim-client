package ch.sbb.rssched.client.pipeline.utils.io;

import lombok.extern.log4j.Log4j2;
import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.scenario.ScenarioUtils;

@Log4j2
public class ScenarioLoader {
    private static final String CONFIG_FILE = "output_config.xml";
    private static final String NETWORK_FILE = "output_network.xml.gz";
    private static final String TRANSIT_SCHEDULE_FILE = "output_transitSchedule.xml.gz";
    private static final String TRANSIT_VEHICLES_FILE = "output_transitVehicles.xml.gz";
    private static final String EVENTS_FILE = "output_events.xml.gz";
    private final String runId;
    private final String inputFolder;

    /**
     * Constructs a ScenarioLoader object with the specified run ID and input folder.
     *
     * @param runId       the ID of the simulation run
     * @param inputFolder the folder containing the output files of the run
     */
    public ScenarioLoader(String runId, String inputFolder) {
        this.runId = runId;
        this.inputFolder = inputFolder;
    }

    public Scenario load(boolean includeNetwork) {
        log.info("Loading scenario {}", runId);
        String configFile = buildPath(CONFIG_FILE);
        String networkFile = includeNetwork ? buildRelativeFileName(NETWORK_FILE) : null;
        String scheduleFile = buildRelativeFileName(TRANSIT_SCHEDULE_FILE);
        String vehiclesFile = buildRelativeFileName(TRANSIT_VEHICLES_FILE);
        Config config = ConfigUtils.loadConfig(configFile);
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
