package ch.sbb.rssched.client.pipeline.request;

import ch.sbb.rssched.client.pipeline.core.ResultSink;
import ch.sbb.rssched.client.pipeline.utils.io.OutputDirectoryManager;
import lombok.extern.log4j.Log4j2;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Request JSON writer
 * <p>
 * Writes the scheduler request to a JSON file in the specified output directory.
 * <p>
 * Note: Although this would be a sink,
 *
 * @author munterfi
 */
@Log4j2
public class RequestJSONWriter implements ResultSink<RequestPipe> {
    private static final String REQUEST_FILE_NAME = "scheduler_request.json";
    private final String outputDirectory;

    public RequestJSONWriter(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    @Override
    public void process(RequestPipe pipe) {
        String filePath = new OutputDirectoryManager(outputDirectory, pipe.getRunId()).buildFilePath(REQUEST_FILE_NAME);
        log.info("Exporting request JSON to {}", filePath);
        try (FileWriter fileWriter = new FileWriter(filePath)) {
            fileWriter.write(pipe.getRequest().toJSON());
        } catch (IOException e) {
            throw new RuntimeException("Error writing the JSON file: " + e.getMessage(), e);
        }
    }
}
