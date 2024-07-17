package ch.sbb.rssched.client.pipeline.scenario;

import ch.sbb.rssched.client.config.selection.FilterStrategy;
import ch.sbb.rssched.client.pipeline.core.Pipeline;

/**
 * The ScenarioPipeline processes a MATSim SIMBA MOBi scenario for the Innosuisse project REP of SBB and ETH Zurich.
 * <p>
 * It extends the Pipeline class and configures the pipeline with the necessary source, filters and sinks. The pipeline
 * loads the scenario, filters the transit lines, masks the scenario to the remaining transit lines, clears attributes
 * on the scenario and exports the processed scenario files. The main method configures and executes the REP pipeline.
 *
 * @author munterfi
 */
public class ScenarioPipeline extends Pipeline<ScenarioPipe> {

    /**
     * Constructs a RepPipeline with the specified parameters.
     *
     * @param instanceId      the ID of the RSSched instance (=request) to solve
     * @param runId           the ID of the scenario run
     * @param inputDirectory  the input directory containing the scenario data (output files of the simulation)
     * @param outputDirectory the output directory to export the processed scenario files
     * @param networkCrs      the coordinate reference system of the network
     * @param filterStrategy  the strategy for filtering transit lines
     */
    public ScenarioPipeline(String instanceId, String runId, String inputDirectory, String outputDirectory, String networkCrs, FilterStrategy filterStrategy) {
        // set source
        super(new ScenarioSource(runId, inputDirectory, networkCrs));
        // filter transit lines
        addFilter(new TransitLineFilter(filterStrategy));
        // mask scenario
        addFilter(new TransitScheduleMask());
        addFilter(new TransitVehicleMask());
        addFilter(new NetworkMask());
        // clear attributes
        addFilter(new AttributeRemover());
        // add sink
        addSink(new LineSelectionCSVWriter(outputDirectory, instanceId));
        addSink(new ScenarioExporter(outputDirectory, instanceId));
    }
}
