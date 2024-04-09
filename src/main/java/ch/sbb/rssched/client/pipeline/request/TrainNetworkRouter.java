package ch.sbb.rssched.client.pipeline.request;

import lombok.extern.log4j.Log4j2;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.router.DijkstraFactory;
import org.matsim.core.router.util.LeastCostPathCalculator;
import org.matsim.core.router.util.TravelDisutility;
import org.matsim.core.trafficmonitoring.FreeSpeedTravelTime;
import org.matsim.pt.transitSchedule.api.TransitStopFacility;
import org.matsim.vehicles.Vehicle;

/**
 * Train network router
 * <p>
 * Calculates the shortest path between two links in the network for train routing.
 *
 * @author munterfi
 * @see TravelDisutility
 */
@Log4j2
public class TrainNetworkRouter {
    private static final Double TOLERANCE = 0.001;
    private final Network network;
    private final double freeSpeedLimit;
    private final LeastCostPathCalculator lpc;

    public TrainNetworkRouter(Network network, double freeSpeedLimit) {
        this.network = network;
        this.freeSpeedLimit = freeSpeedLimit;
        this.lpc = new DijkstraFactory(false).createPathCalculator(network, new TrainTravelDisutility(),
                new FreeSpeedTravelTime());
    }

    /**
     * Calculate the shortest path between two links.
     *
     * @param from The origin transit stop facility.
     * @param to   The destination transit stop facility.
     * @return A record containing the duration in seconds and the distance in meters of the shortest path.
     */
    public PathResult calculate(TransitStopFacility from, TransitStopFacility to) {
        Node fromNode = network.getLinks().get(from.getLinkId()).getToNode();
        Node toNode = network.getLinks().get(to.getLinkId()).getFromNode();
        LeastCostPathCalculator.Path path = lpc.calcLeastCostPath(fromNode, toNode, 0, null, null);
        if (path == null) {
            log.warn("Setting duration and distance to Integer.MAX_VALUE for route from {} to {}", fromNode, toNode);
            return new PathResult(Integer.MAX_VALUE, Integer.MAX_VALUE);
        }
        double travelTime = path.travelCost;
        double travelDistance = extractPathLength(path);
        validateFreeSpeedLimit(travelTime, travelDistance);
        return new PathResult((int) Math.round(travelTime), (int) Math.round(travelDistance));
    }

    private void validateFreeSpeedLimit(double travelTime, double travelDistance) {
        double travelSpeed = (travelDistance / travelTime);
        if (travelSpeed > freeSpeedLimit + TOLERANCE) {
            throw new IllegalArgumentException(
                    String.format("Travel speed (%.2f km/h) exceeds freespeed limit (%.2f km/h)", travelSpeed * 3.6,
                            freeSpeedLimit * 3.6));
        }
    }

    /**
     * Extracts the length of the path in meters from the given LeastCostPathCalculator.Path.
     *
     * @param path The path for which to extract the length.
     * @return The length of the path in meters.
     */
    private double extractPathLength(LeastCostPathCalculator.Path path) {
        double length = 0.0;
        for (Link link : path.links) {
            length += link.getLength();
        }
        return length;
    }

    /**
     * Record representing the result of the shortest path calculation.
     */
    public record PathResult(int duration, int distance) {
    }

    /**
     * The disutility is computed using the link's length and its free speed, representing the time it takes to traverse
     * the link.
     * <p>
     * Minimum travel disutility for any link is set to 0, ensuring no fixed penalty for using any link.
     *
     * @see TravelDisutility
     * @see TrainNetworkRouter
     */
    private final class TrainTravelDisutility implements TravelDisutility {
        @Override
        public double getLinkTravelDisutility(Link link, double time, Person person, Vehicle vehicle) {
            return link.getLength() / Math.min(link.getFreespeed(), freeSpeedLimit);
        }

        @Override
        public double getLinkMinimumTravelDisutility(Link link) {
            return 0;
        }
    }
}
