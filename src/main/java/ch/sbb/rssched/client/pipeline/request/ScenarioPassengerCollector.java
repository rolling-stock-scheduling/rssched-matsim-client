package ch.sbb.rssched.client.pipeline.request;

import ch.sbb.rssched.client.pipeline.core.DataSource;
import ch.sbb.rssched.client.pipeline.passenger.PassengerPipeline;
import ch.sbb.rssched.client.pipeline.scenario.ScenarioPipeline;
import lombok.extern.log4j.Log4j2;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.pt.transitSchedule.api.Departure;
import org.matsim.pt.transitSchedule.api.TransitLine;
import org.matsim.pt.transitSchedule.api.TransitRoute;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Collector class to collect and aggregate results from ScenarioPipeline and PassengerPipeline
 *
 * @author munterfi
 */
@Log4j2
class ScenarioPassengerCollector implements DataSource<RequestPipe> {
    private final String runId;
    private final ScenarioPipeline scenarioPipeline;
    private final PassengerPipeline passengerPipeline;
    private Scenario scenario;
    private Map<Id<TransitLine>, Map<Id<TransitRoute>, Map<Id<Departure>, List<RequestComposer.PassengerCount>>>> passengers;

    public ScenarioPassengerCollector(String runId, ScenarioPipeline scenarioPipeline, PassengerPipeline passengerPipeline) {
        this.runId = runId;
        this.scenarioPipeline = scenarioPipeline;
        this.passengerPipeline = passengerPipeline;
        registerSink(scenarioPipeline);
        registerSink(passengerPipeline);
    }

    @Override
    public RequestPipe fetch() {
        CompletableFuture.allOf(CompletableFuture.runAsync(scenarioPipeline),
                CompletableFuture.runAsync(passengerPipeline)).join();
        return new RequestPipe(runId, scenario, passengers);
    }

    private void registerSink(PassengerPipeline passengerPipeline) {
        passengerPipeline.addSink(pipe -> {
            log.info("Collecting and aggregating results from passenger pipeline");
            passengers = pipe.entries().stream().collect(Collectors.groupingBy(entry -> entry.getTransitLine().getId(),
                    Collectors.groupingBy(entry -> entry.getTransitRoute().getId(),
                            Collectors.groupingBy(entry -> entry.getDeparture().getId(), Collectors.mapping(
                                    entry -> new RequestComposer.PassengerCount(entry.getFromStop(), entry.getToStop(),
                                            entry.getCount(), entry.getSeats()), Collectors.toList())))));
        });
    }

    private void registerSink(ScenarioPipeline scenarioPipeline) {
        scenarioPipeline.addSink(pipe -> {
            log.info("Collecting results from scenario pipeline");
            scenario = pipe.getScenario();
        });
    }
}
