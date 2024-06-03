package ch.sbb.rssched.client.pipeline.response;

import ch.sbb.rssched.client.dto.response.Response;
import ch.sbb.rssched.client.pipeline.core.Filter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Sends the request to the scheduler.
 *
 * @author munterfi
 */
@RequiredArgsConstructor
@Log4j2
public class RequestSender implements Filter<ResponsePipe> {
    public static final String SOLVER_URL_FORMAT = "%s:%d/solve";
    private final String baseUrl;
    private final int port;

    @Override
    public void apply(ResponsePipe pipe) {
        try {
            String url = String.format(SOLVER_URL_FORMAT, baseUrl, port);
            log.info("Sending request to {}...", url);
            String json = pipe.getRequest().toJSON();
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url))
                    .header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(json)).build();
            HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            log.info("Received HTTP response with status code: {}", httpResponse.statusCode());
            if (httpResponse.statusCode() == 200) {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());
                Response response = objectMapper.readValue(httpResponse.body(), Response.class);
                pipe.setResponse(response);
                log.info("Successfully parsed the response: {}", response.getInfo());
            } else {
                throw new IOException("Received response: Status Code = " + httpResponse.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
