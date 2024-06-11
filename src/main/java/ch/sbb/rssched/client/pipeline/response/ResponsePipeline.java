package ch.sbb.rssched.client.pipeline.response;

import ch.sbb.rssched.client.config.RsschedRequestConfig;
import ch.sbb.rssched.client.pipeline.core.Pipeline;
import ch.sbb.rssched.client.pipeline.request.RequestPipeline;

public class ResponsePipeline extends Pipeline<ResponsePipe> {

    public ResponsePipeline(RsschedRequestConfig config, String baseUrl, int port) {
        super(new RequestCollector(new RequestPipeline(config)));
        // add filter
        addFilter(new RequestSender(baseUrl, port));
        // add sink
        addSink(new ResponseJSONWriter(config.getOutputDirectory(), config.getInstanceId()));
    }

}
