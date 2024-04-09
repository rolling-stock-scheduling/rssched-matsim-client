package ch.sbb.rssched.client.pipeline.scenario;

import ch.sbb.rssched.client.pipeline.core.ResultSink;
import ch.sbb.rssched.client.pipeline.utils.io.OutputDirectoryManager;
import lombok.extern.log4j.Log4j2;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.NetworkWriter;
import org.matsim.pt.transitSchedule.api.TransitScheduleWriter;
import org.matsim.vehicles.MatsimVehicleWriter;

/**
 * Exports the processed scenario of the pipeline to the specified output directory.
 * <p>
 * Creates the following files: network.xml.gz, transitSchedule.xml.gz, transitVehicle.xml.gz
 *
 * @author munterfi
 */
@Log4j2
class ScenarioExporter implements ResultSink<ScenarioPipe> {
    private static final String NETWORK_FILE = "network.xml.gz";
    private static final String TRANSIT_SCHEDULE_FILE = "transitSchedule.xml.gz";
    private static final String TRANSIT_VEHICLES_FILE = "transitVehicles.xml.gz";
    private final String outputDirectory;

    /**
     * Constructs a ScenarioExporter with the specified output directory.
     *
     * @param outputDirectory the directory to export the scenario files to
     */
    public ScenarioExporter(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    @Override
    public void process(ScenarioPipe pipe) {
        export(pipe.scenario, pipe.runId);
    }

    private void export(Scenario scenario, String runId) {
        var directoryUtil = new OutputDirectoryManager(outputDirectory, runId);
        new NetworkWriter(scenario.getNetwork()).write(directoryUtil.buildFilePath(NETWORK_FILE));
        new TransitScheduleWriter(scenario.getTransitSchedule()).writeFile(
                directoryUtil.buildFilePath(TRANSIT_SCHEDULE_FILE));
        new MatsimVehicleWriter(scenario.getTransitVehicles()).writeFile(
                directoryUtil.buildFilePath(TRANSIT_VEHICLES_FILE));
        log.info("Processed scenario successfully exported to: " + directoryUtil.getPath());
    }
}
