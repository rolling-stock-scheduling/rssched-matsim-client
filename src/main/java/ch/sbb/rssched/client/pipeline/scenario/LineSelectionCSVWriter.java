package ch.sbb.rssched.client.pipeline.scenario;

import ch.sbb.rssched.client.config.selection.TransitLineSelection;
import ch.sbb.rssched.client.pipeline.core.ResultSink;
import ch.sbb.rssched.client.pipeline.utils.io.OutputDirectoryManager;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.matsim.core.utils.io.IOUtils;

import java.io.IOException;
import java.io.UncheckedIOException;

@Log4j2
class LineSelectionCSVWriter implements ResultSink<ScenarioPipe> {
    private static final String LINE_SELECTION_FILE = "line_selection.csv";
    private static final String[] HEADER = {"group", "transit_line_id", "transit_route_id"};
    private final String outputDirectory;

    /**
     * Constructs a PassengerExporter with the specified output directory.
     *
     * @param outputDirectory the directory to export the passenger file to.
     */
    public LineSelectionCSVWriter(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public static void writeCsv(TransitLineSelection selection, String filename) throws UncheckedIOException {
        try (CSVPrinter csv = new CSVPrinter(IOUtils.getBufferedWriter(filename),
                CSVFormat.DEFAULT.builder().setHeader(HEADER).build())) {
            for (var entry : selection) {
                for (var routeId : entry.routeIds()) {
                    try {
                        csv.print(entry.group());
                        csv.print(entry.lineId().toString());
                        csv.print(routeId.toString());
                        csv.println();
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void process(ScenarioPipe pipe) {
        String filePath = new OutputDirectoryManager(outputDirectory, pipe.runId).buildFilePath(LINE_SELECTION_FILE);
        log.info("Exporting line selection file to {}", filePath);
        writeCsv(pipe.selection, filePath);
    }
}