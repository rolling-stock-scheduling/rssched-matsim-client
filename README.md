# RSSched MATSim Client

Convert MATSim simulation outputs to scheduler service requests. Part of the Innosuisse project Rolling Stock
Scheduling (RSSched) by SBB and ETH Zurich.

The client applies the Pipes and Filters architectural pattern to process the source data in a sequential manner. The
pattern structures a system as a series of processing elements (filters) connected by channels (pipes). Each filter
performs a specific processing task on the data it receives and passes the processed data to the next filter through the
pipes.

## Project Structure

The project has the following package structure:

- `ch.sbb.rssched.client`: Client to bind to the service and send requests.
    - `config`: Configure the request and the pipeline to build it.
        - `selection`: Define strategies to filter the transit lines to be considered.
    - `dto`: implements interface to the rolling stock scheduler of the ETH Zurich.
        - `request`: Build request for scheduler.
        - `response`: Parse scheduler response.
    - `pipeline`:
        - `core`: Provides generic classes for implementing the Pipes and Filters architectural pattern, such as the
          data
          source, pipe (=data transport), filter, pipeline, and result sink.
        - `passenger`: Passenger data analysis pipeline.
        - `request`: Create a scheduler request, by collecting the results from the passenger and scenario pipelines.
        - `scenario`: Scenario processing pipeline, includes various filters and data manipulations, such as scenario
          masking or attribute removal.
        - `utils`: Common helper and utility classes.

## Usage

Set up the `RsschedRequestClient` and send a request to the scheduler service using the `RsschedRequestConfig` to
configure it. Optionally implement a transit line filter strategy.

```java
import ch.sbb.rssched.client.RsschedMatsimClient;
import ch.sbb.rssched.client.config.RsschedRequestConfig;
import ch.sbb.rssched.client.dto.response.Response;

public class Example {

    private static final String SCHEDULER_BASE_URL = "http://localhost";
    private static final int SCHEDULER_PORT = 3000;

    public static void main(String[] args) {
        RsschedRequestConfig config = RsschedRequestConfig.builder()
                .setInputDirectory("path/to/input/directory")
                .setOutputDirectory("path/to/output/directory")
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
```

Alternatively use an XLSX file to configure the request to the scheduler,
see [`RunExample`](src/main/java/ch/sbb/rssched/RunExample.java)
and [`request_config.xlsx`](src/test/resources/ch/sbb/rssched/client/config/request_config.xlsx).

**Note:** There is a hard limit of 500 locations per instance, since the deadhead trip matrix grows exponentially.

---

© 2024 SBB CFF FFS. Licensed under GPL-3.0.
