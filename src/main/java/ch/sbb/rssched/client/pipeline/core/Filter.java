package ch.sbb.rssched.client.pipeline.core;

/**
 * A component that applies a filter to the data.
 *
 * @param <T> The type of pipe (=data transport) used in the pipeline.
 * @author munterfi
 */
@FunctionalInterface
public interface Filter<T extends Pipe> {

    /**
     * Applies the filter to the data.
     *
     * @param pipe The data transport to be filtered.
     */
    void apply(T pipe);
}
