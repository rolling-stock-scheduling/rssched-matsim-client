package ch.sbb.rssched.client.dto.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ResponseTest {

    private String jsonResponse;

    @BeforeEach
    void setUp() throws Exception {
        jsonResponse = new String(Files.readAllBytes(Paths.get("src/test/resources/response_v6.json")));
    }

    @Test
    void fromJson() throws JsonProcessingException {
        Response response = Response.fromJson(jsonResponse);

        assertNotNull(response);
        assertNotNull(response.getInfo());
        assertNotNull(response.getObjectiveValue());
        assertNotNull(response.getSchedule());
    }
}
