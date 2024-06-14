# RSSched MATSim Client

Convert MATSim simulation outputs to [rssched-solver](https://github.com/rolling-stock-scheduling/rssched-solver)
requests. Part of the Innosuisse project Rolling Stock Scheduling (RSSched) by SBB and ETH Zurich.

The client applies the Pipes and Filters architectural pattern to process the source data in a sequential manner. The
pattern structures a system as a series of processing elements (filters) connected by channels (pipes). Each filter
performs a specific processing task on the data it receives and passes the processed data to the next filter through the
pipes.

## Project Structure

The project has the following package structure:

- `ch.sbb.rssched.client`: Client to bind to the service and send requests.
    - `config`: Configure the request and the pipeline to build it.
        - `selection`: Define strategies to filter the transit lines to be considered.
    - `dto`: implements interface to the rolling stock scheduling solver of the ETH Zurich.
        - `request`: Build request for solver.
        - `response`: Parse solver response.
    - `pipeline``:`
        - `core`: Provides generic classes for implementing the Pipes and Filters architectural pattern, such as the
          data
          source, pipe (=data transport), filter, pipeline, and result sink.
        - `passenger`: Passenger data analysis pipeline.
        - `scenario`: Scenario processing pipeline, includes various filters and data manipulations, such as scenario
          masking or attribute removal.
        - `request`: Create a solver request, by collecting the results from the passenger and scenario pipelines.
        - `response`: Send the request to the solver and await response.
        - `utils`: Common helper and utility classes.

## Usage

To get started with the whole RSSched project, have a look at this [step-by-step instruction](https://github.com/rolling-stock-scheduling/.github/blob/main/getting_started.md).

### Using Command-Line Application

Read the request configuration from an Excel file and optionally send the request to the solver using the
RsschedMatsimClient:

```sh
mvn exec:java -Dexec.args="path/to/config_file.xlsx -h localhost -p 3000 -d"
```

Options:

- **Required:**
    - `config_file`: Path to the Excel configuration file.
- **Optional:**
    - `-h / --host`: Scheduler base URL (default: "http://localhost").
    - `-p / --port`: Scheduler port (default: 3000).
    - `-d / --dry-run`: If present, do not send the request to the solver (default: false).

See [kelheim-v3.0-25pct.rssched_request_config.xlsx](integration-test/input/de/kelheim/kelheim-v3.0/25pct/kelheim-v3.0-25pct.rssched_request_config.xlsx)
for reference of a request configuration.

**Note**: To be able to run the integration test using the command line app, first execute the integration tests, which
downloads the needed matsim run outputs, see the paragraph **Testing** below.

### Using Client in Java

Set up the `RsschedRequestClient` and send a request to the rolling stock scheduling solver service using
the `RsschedRequestConfig` to configure it. Optionally implement a transit line filter strategy.

```java
import ch.sbb.rssched.client.RsschedMatsimClient;
import ch.sbb.rssched.client.config.RsschedRequestConfig;
import ch.sbb.rssched.client.dto.response.Response;

public class RunExample {

    private static final String SCHEDULER_BASE_URL = "http://localhost";
    private static final int SCHEDULER_PORT = 3000;

    public static void main(String[] args) {
        RsschedRequestConfig config = RsschedRequestConfig.builder()
                .setInputDirectory("path/to/input/directory")
                .setOutputDirectory("path/to/output/directory")
                .setRunId("runId")
                .setInstanceId("rss001")
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
```

Alternatively use an Excel file to configure the request to the solver:

```java
import ch.sbb.rssched.client.RsschedMatsimClient;
import ch.sbb.rssched.client.config.RsschedRequestConfig;
import ch.sbb.rssched.client.config.RsschedRequestConfigReader;
import ch.sbb.rssched.client.dto.response.Response;

import java.io.IOException;

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
```

**Note:** There is a hard limit of 500 locations per instance, since the deadhead trip matrix grows exponentially.

## Testing

Run the unit tests:

```sh
mvn test
```

Run the integration test to see the pipeline in action (needs a running solver on localhost and port 3000):

```sh
mvn verify -Dit.test=RsschedMatsimClientIT
```

---

© 2024 SBB CFF FFS. Licensed under GPL-3.0.
