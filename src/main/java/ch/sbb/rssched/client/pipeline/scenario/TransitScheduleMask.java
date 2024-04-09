package ch.sbb.rssched.client.pipeline.scenario;

import ch.sbb.rssched.client.pipeline.core.Filter;
import lombok.extern.log4j.Log4j2;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.pt.transitSchedule.api.TransitLine;
import org.matsim.pt.transitSchedule.api.TransitRouteStop;
import org.matsim.pt.transitSchedule.api.TransitSchedule;

import java.util.Set;

/**
 * Masks the transit schedule based on transit line IDs of interest.
 *
 * @author munterfi
 */
@Log4j2
class TransitScheduleMask implements Filter<ScenarioPipe> {

    private static void maskTransitLines(Set<Id<TransitLine>> transitLineIds, TransitSchedule transitSchedule) {
        var transitLinesToRemove = transitSchedule.getTransitLines().values().stream()
                .filter(transitLine -> !transitLineIds.contains(transitLine.getId())).toList();
        transitLinesToRemove.forEach(transitSchedule::removeTransitLine);
    }

    private static void maskTransitStopFacilities(Set<Id<TransitLine>> transitLineIds, TransitSchedule transitSchedule) {
        var transitStopsToKeep = transitLineIds.stream().flatMap(
                transitLineId -> transitSchedule.getTransitLines().get(transitLineId).getRoutes().values().stream()
                        .flatMap(transitRoute -> transitRoute.getStops().stream()
                                .map(TransitRouteStop::getStopFacility))).distinct().toList();
        var transitStopsToRemove = transitSchedule.getFacilities().values().stream()
                .filter(stopFacility -> !transitStopsToKeep.contains(stopFacility)).toList();
        transitStopsToRemove.forEach(transitSchedule::removeStopFacility);
    }

    private static void truncateMinimalTransferTimes(TransitSchedule transitSchedule) {
        var minimalTransferTimes = transitSchedule.getMinimalTransferTimes();
        var iterator = minimalTransferTimes.iterator();
        while (iterator.hasNext()) {
            iterator.next();
            var fromStop = iterator.getFromStopId();
            var toStop = iterator.getToStopId();
            minimalTransferTimes.remove(fromStop, toStop);
        }
    }

    @Override
    public void apply(ScenarioPipe pipe) {
        maskTransitSchedule(pipe.scenario, pipe.selection.getLineIds());
    }

    private void maskTransitSchedule(Scenario scenario, Set<Id<TransitLine>> transitLineIds) {
        var transitSchedule = scenario.getTransitSchedule();
        log.info("Masking transit schedule (lines: {}, stops: {})", transitSchedule.getTransitLines().size(),
                transitSchedule.getFacilities().size());
        maskTransitLines(transitLineIds, transitSchedule);
        maskTransitStopFacilities(transitLineIds, transitSchedule);
        truncateMinimalTransferTimes(transitSchedule);
        log.info("Done (remaining lines: {}, stops: {})", transitSchedule.getTransitLines().size(),
                transitSchedule.getFacilities().size());
    }
}
