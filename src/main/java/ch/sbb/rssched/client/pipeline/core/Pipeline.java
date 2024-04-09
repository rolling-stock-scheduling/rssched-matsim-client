package ch.sbb.rssched.client.pipeline.core;

import lombok.extern.log4j.Log4j2;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class representing a pipeline in the Pipes and Filters architecture.
 * <p>
 * It orchestrates the overall process by executing the filters and result sinks on the data transport in the order they
 * are added. Subclasses should implement specific pipeline functionality.
 * <p>
 * Note: A pipeline has only one data source but can have multiple filters and sinks.
 *
 * @param <T> The type of pipe (=data transport) used in the pipeline.
 * @author munterfi
 * @see Pipe
 * @see DataSource
 * @see Filter
 * @see ResultSink
 */
@Log4j2
public abstract class Pipeline<T extends Pipe> implements Runnable {
    private final DataSource<T> source;
    private final List<Filter<T>> filters = new ArrayList<>();
    private final List<ResultSink<T>> sinks = new ArrayList<>();

    /**
     * Constructs a Pipeline with the specified data source.
     *
     * @param source The data source for the pipeline.
     */
    protected Pipeline(DataSource<T> source) {
        log.info("Set source: {}", source.getClass().getName());
        this.source = source;
    }

    /**
     * Runs the pipeline by executing filters and result sinks on the data transport.
     */
    @Override
    public void run() {
        log.info("Start {} with {} filter(s) and {} sink(s)", getClass().getSimpleName(), filters.size(), sinks.size());
        var startTime = Instant.now();
        T pipe = source.fetch();
        filters.forEach(filter -> filter.apply(pipe));
        sinks.forEach(sink -> sink.process(pipe));
        var endTime = Instant.now();
        log.info("{} completed successfully in {} seconds", getClass().getSimpleName(),
                Duration.between(startTime, endTime).getSeconds());
    }

    /**
     * Adds a filter to the pipeline.
     *
     * @param filter The filter to be added.
     */
    public void addFilter(Filter<T> filter) {
        log.info("Add filter: {}", filter.getClass().getName());
        filters.add(filter);
    }

    /**
     * Adds a result sink to the pipeline.
     *
     * @param sink The result sink to be added.
     */
    public void addSink(ResultSink<T> sink) {
        log.info("Add sink: {}", sink.getClass().getName());
        sinks.add(sink);
    }
}
