package ch.sbb.rssched.client.pipeline.response;

import ch.sbb.rssched.client.dto.request.Request;
import ch.sbb.rssched.client.dto.response.Response;
import ch.sbb.rssched.client.pipeline.core.Pipe;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
public class ResponsePipe implements Pipe {

    private final String runId;
    private final Request request;
    @Setter
    private Response response;

}
