package ch.sbb.rssched.client.pipeline.core;

/**
 * A component that provides data for processing.
 *
 * @param <T> The type of pipe (=data transport) used in the pipeline.
 * @author munterfi
 */
@FunctionalInterface
public interface DataSource<T extends Pipe> {

    /**
     * Fetches the data from the data source and wraps it inside a data transport object for transportation in the
     * pipeline.
     *
     * @return The data transport containing the fetched data.
     */
    T fetch();
}
