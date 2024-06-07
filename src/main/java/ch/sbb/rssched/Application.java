package ch.sbb.rssched;

import ch.sbb.rssched.client.RsschedMatsimClient;
import ch.sbb.rssched.client.RsschedMatsimRequestGenerator;
import ch.sbb.rssched.client.config.RsschedRequestConfig;
import ch.sbb.rssched.client.config.RsschedRequestConfigReader;
import ch.sbb.rssched.client.dto.response.Response;

import java.io.IOException;

/**
 * Read the request configuration from an Excel file and optionally send the request to the solver using the
 * RsschedMatsimClient
 *
 * @author munterfi
 */
public class Application {

    // TODO: Export also RSSched configuration excel to output folder

    // TODO: Use instance ID for file paths.

    // TODO: Set max deadhead trip to beeline distance times a factor.

    // TODO: make primary arg
    private static final String REQUEST_CONFIG_XLSX = "integration-test/input/de/kelheim/kelheim-v3.0/25pct/kehlheim-v3.0-25pct.rssched_request_config.xlsx";
    // TODO: Make cmd options
    private static final String SCHEDULER_BASE_URL = "http://localhost";
    private static final int SCHEDULER_PORT = 3000;
    private static final boolean SEND_TO_SOLVER = false;

    public static void main(String[] args) throws IOException {
        RsschedRequestConfig config = new RsschedRequestConfigReader().readExcelFile(REQUEST_CONFIG_XLSX);

        if (SEND_TO_SOLVER) {
            // create request and send to solver
            RsschedMatsimClient client = new RsschedMatsimClient(SCHEDULER_BASE_URL, SCHEDULER_PORT);
            Response response = client.process(config);
        } else {
            // only create request and export as JSON
            new RsschedMatsimRequestGenerator().process(config);
        }

    }

}
