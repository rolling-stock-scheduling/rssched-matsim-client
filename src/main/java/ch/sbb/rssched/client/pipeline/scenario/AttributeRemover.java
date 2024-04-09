package ch.sbb.rssched.client.pipeline.scenario;

import ch.sbb.rssched.client.pipeline.core.Filter;
import lombok.extern.log4j.Log4j2;
import org.matsim.api.core.v01.Scenario;
import org.matsim.utils.objectattributes.attributable.Attributable;

import java.util.Map;

/**
 * Clears all attributes from the network, transit schedule and transit vehicles in a scenario.
 *
 * @author munterfi
 */
@Log4j2
class AttributeRemover implements Filter<ScenarioPipe> {

    private static <K, V extends Attributable> void clearAttributes(Map<K, V> map) {
        map.values().forEach(value -> value.getAttributes().clear());
    }

    @Override
    public void apply(ScenarioPipe pipe) {
        clearAll(pipe.scenario);
    }

    private void clearAll(Scenario scenario) {
        log.info("Clearing attributes");
        clearAttributes(scenario.getTransitSchedule().getTransitLines());
        clearAttributes(scenario.getTransitSchedule().getFacilities());
        clearAttributes(scenario.getTransitVehicles().getVehicleTypes());
        clearAttributes(scenario.getNetwork().getLinks());
        clearAttributes(scenario.getNetwork().getNodes());
        scenario.getTransitSchedule().getTransitLines().values()
                .forEach(transitLine -> clearAttributes(transitLine.getRoutes()));
    }
}
