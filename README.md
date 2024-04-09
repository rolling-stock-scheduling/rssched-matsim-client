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

public class Example {

    public static void main(String[] args) {
        RsschedRequestConfig config = RsschedRequestConfig.builder().
                setInputDirectory("path/to/input/directory")
                .setOutputDirectory("path/to/output/directory").
                setRunId("runId")
                // optionally set transit line filter, default is no filtering
                .setFilterStrategy(scenario -> {
                    // implementation...
                    return null;
                }).buildWithDefaults();

        RsschedMatsimClient client = new RsschedMatsimClient("localhost", 3000);

        client.process(config);
    }

}
```

---

© 2024 SBB CFF FFS. Licensed under GPL-3.0.
