package ch.sbb.rssched.client;

import ch.sbb.rssched.client.config.RsschedRequestConfig;
import ch.sbb.rssched.client.dto.response.Response;
import ch.sbb.rssched.client.pipeline.response.ResponsePipeline;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Rolling stock scheduling service client
 * <p>
 * Converts MATSim simulation run outputs to requests and sends them to the RSS service.
 *
 * @author munterfi
 */
@Log4j2
@RequiredArgsConstructor
public class RsschedMatsimClient {
    private final String baseUrl;
    private final int port;

    public Response process(RsschedRequestConfig config) {
        AtomicReference<Response> response = new AtomicReference<>();

        ResponsePipeline pipeline = new ResponsePipeline(config, baseUrl, port);
        pipeline.addSink(pipe -> response.set(pipe.getResponse()));
        pipeline.run();

        return response.get();
    }
}
