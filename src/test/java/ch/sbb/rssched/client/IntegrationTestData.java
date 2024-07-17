package ch.sbb.rssched.client;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

@RequiredArgsConstructor
@Log4j2
public class IntegrationTestData {

    private static final String BASE_URL = "https://svn.vsp.tu-berlin.de/repos/public-svn/matsim/scenarios/countries/de/kelheim/kelheim-v3.0/output/25pct/";
    private static final String[] FILE_NAMES = {"kelheim-v3.0-25pct.output_events.xml.gz", "kelheim-v3.0-25pct.output_network.xml.gz", "kelheim-v3.0-25pct.output_transitSchedule.xml.gz", "kelheim-v3.0-25pct.output_transitVehicles.xml.gz"};
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
        ensureDirectory(MatsimRun.INPUT_DIRECTORY);
        ensureDirectory(MatsimRun.OUTPUT_DIRECTORY);

        for (String fileName : FILE_NAMES) {
            String outputFilePath = MatsimRun.INPUT_DIRECTORY + fileName;
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

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    final static class MatsimRun {
        public static final String INPUT_DIRECTORY = "integration-test/input/de/kelheim/kelheim-v3.0/25pct/";
        public static final String OUTPUT_DIRECTORY = "integration-test/output/de/kelheim/kelheim-v3.0/25pct/";
        public static final String ID = "kelheim-v3.0-25pct";
        public static final String CRS = "EPSG:25832";
        public static final double SAMPLE_SIZE = 0.25;
        public static final Set<String> LOCATIONS_IN_KEHLHEIM = Set.of("short_10302.5", "regio_275062", "short_10302.6",
                "regio_289114.2", "regio_289114.4", "regio_92976", "short_10302.3", "short_10302.4", "regio_275062.1",
                "short_9488", "short_10302.1", "short_10302.2", "short_9488.1", "short_9488.4", "short_9488.5",
                "short_9488.2", "short_9488.3", "short_9488.6", "short_10302", "short_12323", "regio_289114",
                "regio_289114.1", "regio_289114.3", "regio_185728");
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    final static class MaintenanceSlots {
        public static final LocalDateTime DAY_SHIFT_START = LocalDateTime.of(LocalDate.now(), LocalTime.of(8, 0, 0));
        public static final LocalDateTime DAY_SHIFT_END = LocalDateTime.of(LocalDate.now(), LocalTime.of(16, 0, 0));
        public static final LocalDateTime NIGHT_SHIFT_START = LocalDateTime.of(LocalDate.now(), LocalTime.of(20, 0, 0));
        public static final LocalDateTime NIGHT_SHIFT_END = LocalDateTime.of(LocalDate.now().plusDays(1),
                LocalTime.of(4, 0, 0));
        public static final int TRACK_COUNT = 3;
        public static final Set<String> LOCATIONS = Set.of("regio_67809", "regio_81147", "regio_293170", "short_9666",
                "short_9804", "short_1119");
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    final static class Depots {
        public static final Set<String> LOCATIONS = Set.of("regio_141134", "regio_145538", "regio_147682",
                "regio_151384", "regio_172317", "regio_215502", "regio_293170", "short_1119", "short_3740",
                "short_5972", "short_6900");
        public static final int CAPACITY = 10;
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    final static class Shunting {
        public static final Set<String> LOCATIONS = Set.of("regio_141134", "regio_166759", "regio_314854",
                "regio_215502", "short_1119", "short_6474", "short_5972", "short_9804");
    }
}
