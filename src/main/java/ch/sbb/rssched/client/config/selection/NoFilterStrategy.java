package ch.sbb.rssched.client.config.selection;

import lombok.extern.log4j.Log4j2;
import org.matsim.api.core.v01.Scenario;

/**
 * No filter strategy
 * <p>
 * Don't apply a filter to the transit lines.
 *
 * @author munterfi
 */
@Log4j2
public class NoFilterStrategy implements FilterStrategy {

    @Override
    public TransitLineSelection filter(Scenario scenario) {
        log.info("Omit filtering the transit lines");
        var selection = new TransitLineSelection();
        scenario.getTransitSchedule().getTransitLines().forEach((transitLineId, transitLine) -> transitLine.getRoutes()
                .forEach((transitRouteId, transitRoute) -> selection.add(TransitLineSelection.NO_GROUP, transitLineId,
                        transitRouteId)));
        return selection;
    }

}
