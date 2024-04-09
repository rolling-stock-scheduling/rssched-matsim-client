package ch.sbb.rssched.client.pipeline.utils.io;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.io.File;

/**
 * Manages the output directory and file naming.
 *
 * @author munterfi
 */
@Log4j2
public class OutputDirectoryManager {
    private static final String DIRECTORY_PREFIX = "rep";
    @Getter
    private final String path;
    private final String runId;

    public OutputDirectoryManager(String outputDirectory, String runId) {
        this.path = String.format("%s/%s_%s", outputDirectory, DIRECTORY_PREFIX, runId);
        this.runId = runId;
        createDirectory(path);
    }

    private static void createDirectory(String path) {
        File directory = new File(path);
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (!created) {
                log.error("Failed to create directory: {}", path);
            }
        }
    }

    public String buildFilePath(String fileName) {
        return String.format("%s/%s.%s", path, runId, fileName);
    }

}
