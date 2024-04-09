package ch.sbb.rssched.client;

import ch.sbb.rssched.client.config.RsschedRequestConfig;
import ch.sbb.rssched.client.dto.response.Response;
import ch.sbb.rssched.client.pipeline.request.RequestPipeline;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * Rolling stock scheduling service client
 * <p>
 * Converts MATSim simulation run outputs to requests to the RSS service.
 *
 * @author munterfi
 */
@Log4j2
@RequiredArgsConstructor
public class RsschedMatsimClient {
    private final String host;
    private final int port;

    public Response process(RsschedRequestConfig config) {
        RequestPipeline pipeline = new RequestPipeline(config);
        pipeline.run();
        // TODO: Implement POST request
        return null;
    }
}
