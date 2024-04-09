package ch.sbb.rssched.client.pipeline.scenario;

import ch.sbb.rssched.client.config.selection.TransitLineSelection;
import ch.sbb.rssched.client.pipeline.core.Pipe;
import lombok.Getter;
import org.matsim.api.core.v01.Scenario;

/**
 * A container for transporting a scenario, the run id and the transit lines of interest between filters and result
 * sinks.
 *
 * @author munterfi
 */
public class ScenarioPipe implements Pipe {
    final String runId;
    @Getter
    final Scenario scenario;
    TransitLineSelection selection;

    ScenarioPipe(String runId, Scenario scenario) {
        this.runId = runId;
        this.scenario = scenario;
    }

}
