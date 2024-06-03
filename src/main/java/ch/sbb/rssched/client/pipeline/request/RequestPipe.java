package ch.sbb.rssched.client.pipeline.request;

import ch.sbb.rssched.client.dto.request.Request;
import ch.sbb.rssched.client.pipeline.core.Pipe;
import lombok.Getter;
import lombok.Setter;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.pt.transitSchedule.api.Departure;
import org.matsim.pt.transitSchedule.api.TransitLine;
import org.matsim.pt.transitSchedule.api.TransitRoute;

import java.util.List;
import java.util.Map;

/**
 * A container for transporting a scenario, the run id and the final request.
 *
 * @author munterfi
 */
@Getter
public class RequestPipe implements Pipe {

    private final String runId;
    private final Scenario scenario;
    private final Map<Id<TransitLine>, Map<Id<TransitRoute>, Map<Id<Departure>, List<RequestComposer.PassengerCount>>>> passengers;
    @Setter
    private Request request;

    RequestPipe(String runId, Scenario scenario, Map<Id<TransitLine>, Map<Id<TransitRoute>, Map<Id<Departure>, List<RequestComposer.PassengerCount>>>> passengers) {
        this.runId = runId;
        this.scenario = scenario;
        this.passengers = passengers;
    }

}
