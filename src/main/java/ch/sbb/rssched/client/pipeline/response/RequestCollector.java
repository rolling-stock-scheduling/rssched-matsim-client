package ch.sbb.rssched.client.pipeline.response;

import ch.sbb.rssched.client.dto.request.Request;
import ch.sbb.rssched.client.pipeline.core.DataSource;
import ch.sbb.rssched.client.pipeline.request.RequestPipeline;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class RequestCollector implements DataSource<ResponsePipe> {

    private final RequestPipeline requestPipeline;
    private String runId;
    private Request request;

    public RequestCollector(RequestPipeline requestPipeline) {
        this.requestPipeline = requestPipeline;
        registerSink(requestPipeline);
    }

    @Override
    public ResponsePipe fetch() {
        requestPipeline.run();
        return new ResponsePipe(runId, request);
    }

    private void registerSink(RequestPipeline requestPipeline) {
        requestPipeline.addSink(pipe -> {
            log.info("Collecting request from passenger pipeline");
            runId = pipe.getRunId();
            request = pipe.getRequest();
        });
    }

}
