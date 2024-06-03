package ch.sbb.rssched;

import ch.sbb.rssched.client.RsschedMatsimClient;
import ch.sbb.rssched.client.config.RsschedRequestConfig;
import ch.sbb.rssched.client.config.RsschedRequestConfigReader;
import ch.sbb.rssched.client.dto.response.Response;

import java.io.IOException;

/**
 * Read the request configuration from an Excel file and send the request to the solver using the RsschedMatsimClient
 *
 * @author munterfi
 */
public class RunExample {

    private static final String REQUEST_CONFIG_XLSX = "rssched_request_config.xlsx";
    private static final String SCHEDULER_BASE_URL = "http://localhost";
    private static final int SCHEDULER_PORT = 3000;

    public static void main(String[] args) throws IOException {
        RsschedRequestConfig config = new RsschedRequestConfigReader().readExcelFile(REQUEST_CONFIG_XLSX);
        RsschedMatsimClient client = new RsschedMatsimClient(SCHEDULER_BASE_URL, SCHEDULER_PORT);
        Response response = client.process(config);
    }

}
