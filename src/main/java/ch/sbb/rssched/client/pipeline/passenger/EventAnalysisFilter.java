package ch.sbb.rssched.client.pipeline.passenger;

import ch.sbb.rssched.client.pipeline.core.Filter;
import lombok.extern.log4j.Log4j2;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.events.EventsUtils;
import org.matsim.core.events.MatsimEventsReader;
import org.matsim.core.events.handler.EventHandler;

/**
 * Applies the EventAnalysis on the PassengerPipe data container.
 *
 * @author munterfi
 */
@Log4j2
class EventAnalysisFilter implements Filter<PassengerPipe> {

    private final int seatDurationThreshold;

    EventAnalysisFilter(int seatDurationThreshold) {
        this.seatDurationThreshold = seatDurationThreshold;
    }

    private static void runEventAnalysis(String eventFile, EventHandler eventAnalysis) {
        EventsManager events = EventsUtils.createEventsManager();
        events.addHandler(eventAnalysis);
        events.initProcessing();
        new MatsimEventsReader(events).readFile(eventFile);
        events.finishProcessing();
    }

    @Override
    public void apply(PassengerPipe pipe) {
        log.info("Starting event analysis for simulation run {}", pipe.runId());
        var passengerEventAnalysis = new EventAnalysis(pipe.scenario(), pipe.transitLineIds(), seatDurationThreshold);
        runEventAnalysis(pipe.eventsFile(), passengerEventAnalysis);
        pipe.entries().addAll(passengerEventAnalysis.getEntries());
    }
}
