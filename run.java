import ch.sbb.rssched.client.RsschedMatsimClient;
import ch.sbb.rssched.client.config.RsschedRequestConfig;
import ch.sbb.rssched.client.dto.response.Response;

public class Example {

    private static final String SCHEDULER_BASE_URL = "http://localhost";
    private static final int SCHEDULER_PORT = 3000;

    public static void main(String[] args) {
        RsschedRequestConfig config = RsschedRequestConfig.builder()
                .setInputDirectory("../matsim_run")
                .setOutputDirectory("..")
                .setRunId("runId")
                // optionally set transit line filter, default is no filtering
                .setFilterStrategy(scenario -> {
                    // implementation...
                    return null;
                }).buildWithDefaults();

        // optionally set sample size factor, default is 1.0
        config.getGlobal().setSampleSize(0.25);

        RsschedMatsimClient client = new RsschedMatsimClient(SCHEDULER_BASE_URL, SCHEDULER_PORT);
        Response response = client.process(config);
    }

} 
