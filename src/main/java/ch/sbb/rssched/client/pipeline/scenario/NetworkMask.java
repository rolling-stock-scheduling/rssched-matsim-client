package ch.sbb.rssched.client.pipeline.scenario;

import ch.sbb.rssched.client.pipeline.core.Filter;
import lombok.extern.log4j.Log4j2;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.pt.transitSchedule.api.TransitLine;
import org.matsim.pt.transitSchedule.api.TransitSchedule;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Masks the network based on transit line IDs of interest.
 *
 * @author munterfi
 */
@Log4j2
class NetworkMask implements Filter<ScenarioPipe> {

    private static List<Id<Link>> maskLinks(Set<Id<TransitLine>> transitLineIds, TransitSchedule transitSchedule, Network network) {
        Set<Id<Link>> linkIdsToKeep = transitLineIds.stream().flatMap(
                transitLineId -> transitSchedule.getTransitLines().get(transitLineId).getRoutes().values().stream()
                        .flatMap(transitRoute -> {
                            Set<Id<Link>> linkIds = new HashSet<>(transitRoute.getRoute().getLinkIds());
                            linkIds.add(transitRoute.getStops().get(0).getStopFacility().getLinkId());
                            linkIds.add(
                                    transitRoute.getStops().get(transitRoute.getStops().size() - 1).getStopFacility()
                                            .getLinkId());
                            return linkIds.stream();
                        })).collect(Collectors.toSet());

        List<Id<Link>> linkIdsToRemove = network.getLinks().keySet().stream()
                .filter(linkId -> !linkIdsToKeep.contains(linkId)).toList();

        linkIdsToRemove.forEach(network::removeLink);

        return new ArrayList<>(linkIdsToKeep);
    }

    private static void maskNodes(Network network, List<Id<Link>> linkIdsToKeep) {
        var nodeIdsToKeep = linkIdsToKeep.stream().flatMap(
                linkId -> Stream.of(network.getLinks().get(linkId).getFromNode().getId(),
                        network.getLinks().get(linkId).getToNode().getId())).distinct().toList();
        var nodeIdsToRemove = network.getNodes().keySet().stream().filter(nodeId -> !nodeIdsToKeep.contains(nodeId))
                .toList();
        nodeIdsToRemove.forEach(network::removeNode);
    }

    @Override
    public void apply(ScenarioPipe pipe) {
        maskNetwork(pipe.scenario, pipe.selection.getLineIds());
    }

    private void maskNetwork(Scenario scenario, Set<Id<TransitLine>> transitLineIds) {
        var transitSchedule = scenario.getTransitSchedule();
        var network = scenario.getNetwork();
        log.info("Masking network (nodes: {}, links: {})", network.getNodes().size(), network.getLinks().size());
        maskNodes(network, maskLinks(transitLineIds, transitSchedule, network));
        log.info("Done (remaining nodes: {}, links: {})", network.getNodes().size(), network.getLinks().size());
    }
}
