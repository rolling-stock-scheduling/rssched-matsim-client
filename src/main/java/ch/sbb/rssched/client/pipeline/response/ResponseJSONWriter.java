package ch.sbb.rssched.client.pipeline.response;

import ch.sbb.rssched.client.pipeline.core.ResultSink;
import ch.sbb.rssched.client.pipeline.utils.io.OutputDirectoryManager;
import lombok.extern.log4j.Log4j2;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Response JSON writer
 * <p>
 * Writes the scheduler response to a JSON file in the specified output directory.
 *
 * @author munterfi
 */
@Log4j2
public class ResponseJSONWriter implements ResultSink<ResponsePipe> {
    private static final String RESPONSE_FILE_NAME = "scheduler_response.json";
    private final String outputDirectory;
    private final String instanceId;

    public ResponseJSONWriter(String outputDirectory, String instanceId) {
        this.outputDirectory = outputDirectory;
        this.instanceId = instanceId;
    }

    @Override
    public void process(ResponsePipe pipe) {
        String filePath = new OutputDirectoryManager(outputDirectory, pipe.getRunId(), instanceId).buildFilePath(
                RESPONSE_FILE_NAME);
        log.info("Exporting response JSON to {}", filePath);
        try (FileWriter fileWriter = new FileWriter(filePath)) {
            fileWriter.write(pipe.getResponse().toJSON());
        } catch (IOException e) {
            throw new RuntimeException("Error writing the JSON file: " + e.getMessage(), e);
        }
    }
}
