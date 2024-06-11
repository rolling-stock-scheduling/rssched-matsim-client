package ch.sbb.rssched.client;

import ch.sbb.rssched.client.config.RsschedRequestConfig;
import ch.sbb.rssched.client.dto.request.Request;
import ch.sbb.rssched.client.pipeline.request.RequestPipeline;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Generates a rolling stock scheduling solver request from a MATSim run output and exports it to a JSON file.
 */
public class RsschedMatsimRequestGenerator {

    public Request process(RsschedRequestConfig config) {
        AtomicReference<Request> request = new AtomicReference<>();

        var pipeline = new RequestPipeline(config);
        pipeline.addSink(pipe -> request.set(pipe.getRequest()));
        pipeline.run();

        return request.get();
    }

}
