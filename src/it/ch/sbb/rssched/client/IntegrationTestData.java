package ch.sbb.rssched.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

@RequiredArgsConstructor
@Log4j2
public class IntegrationTestData {

    public static final String IT_INPUT_DIRECTORY = "integration-test/input/de/kelheim/kelheim-v3.0/25pct/";
    public static final String IT_OUTPUT_DIRECTORY = "integration-test/output/de/kelheim/kelheim-v3.0/25pct/";
    public static final String RUN_ID = "kelheim-v3.0-25pct";
    private static final String BASE_URL = "https://svn.vsp.tu-berlin.de/repos/public-svn/matsim/scenarios/countries/de/kelheim/kelheim-v3.0/output/25pct/";
    private static final String[] FILE_NAMES = {"kelheim-v3.0-25pct.output_events.xml.gz", "kelheim-v3.0-25pct.output_network.xml.gz", "kelheim-v3.0-25pct.output_transitSchedule.xml.gz", "kelheim-v3.0-25pct.output_transitVehicles.xml.gz", "kelheim-v3.0-25pct.output_config.xml"};
    private final boolean overwrite;

    private static void downloadFile(String url, String outputFilePath) throws IOException {
        log.info("Downloading: {} ...", url);
        try (BufferedInputStream in = new BufferedInputStream(
                new URL(url).openStream()); FileOutputStream fileOutputStream = new FileOutputStream(outputFilePath)) {
            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
        }
    }

    private static boolean fileExists(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    public void setup() throws IOException {
        ensureDirectory(IT_INPUT_DIRECTORY);
        ensureDirectory(IT_OUTPUT_DIRECTORY);

        for (String fileName : FILE_NAMES) {
            String outputFilePath = IT_INPUT_DIRECTORY + fileName;
            if (overwrite || !fileExists(outputFilePath)) {
                downloadFile(BASE_URL + fileName, outputFilePath);
            }
        }
    }

    private void ensureDirectory(String path) {
        File directory = new File(path);
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (created) {
                log.info("Output directory created: {}", path);
            } else {
                log.error("Failed to create output directory: {}", path);
            }
        }
    }
}
