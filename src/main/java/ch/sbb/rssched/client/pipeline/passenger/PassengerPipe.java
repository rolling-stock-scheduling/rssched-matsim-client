package ch.sbb.rssched.client.pipeline.passenger;

import ch.sbb.rssched.client.pipeline.core.Pipe;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.pt.transitSchedule.api.TransitLine;

import java.util.List;
import java.util.Set;

/**
 * A container for transporting data in the PassengerPipeline.
 *
 * @param runId          The identifier of the run associated with the events.
 * @param eventsFile     The path of the events file.
 * @param scenario       The scenario representing the simulation run.
 * @param transitLineIds The set of transit line IDs to consider in the analysis.
 * @param entries        An empty list for storing the results of the event analysis.
 * @author munterfi
 */
public record PassengerPipe(String runId, String eventsFile, Scenario scenario, Set<Id<TransitLine>> transitLineIds,
                            List<EventAnalysis.Entry> entries) implements Pipe {
}
