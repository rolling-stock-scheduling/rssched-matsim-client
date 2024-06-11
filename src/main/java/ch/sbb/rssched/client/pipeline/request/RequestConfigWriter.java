package ch.sbb.rssched.client.pipeline.request;

import ch.sbb.rssched.client.config.RsschedRequestConfig;
import ch.sbb.rssched.client.pipeline.core.ResultSink;
import ch.sbb.rssched.client.pipeline.utils.io.OutputDirectoryManager;
import lombok.extern.log4j.Log4j2;

import java.io.FileWriter;
import java.io.IOException;


/**
 * Request config JSON writer
 * <p>
 * Writes the scheduler request configuration to a JSON file in the specified output directory.
 *
 * @author munterfi
 */
@Log4j2
public class RequestConfigWriter implements ResultSink<RequestPipe> {
    private static final String REQUEST_CONFIG_FILE_NAME = "scheduler_request_config.json";
    private final RsschedRequestConfig config;

    public RequestConfigWriter(RsschedRequestConfig config) {
        this.config = config;
    }

    @Override
    public void process(RequestPipe pipe) {
        String filePath = new OutputDirectoryManager(config.getOutputDirectory(), pipe.getRunId(),
                config.getInstanceId()).buildFilePath(REQUEST_CONFIG_FILE_NAME);
        log.info("Exporting request config JSON to {}", filePath);
        try (FileWriter fileWriter = new FileWriter(filePath)) {
            fileWriter.write(config.toJSON());
        } catch (IOException e) {
            throw new RuntimeException("Error writing the JSON file: " + e.getMessage(), e);
        }
    }
}
