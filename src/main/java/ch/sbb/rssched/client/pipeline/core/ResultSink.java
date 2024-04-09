package ch.sbb.rssched.client.pipeline.core;

/**
 * A component that serves as a result sink for processed data.
 * <p>
 * It defines a method to export the data transport.
 *
 * @param <T> The type of pipe (=data transport) used in the pipeline.
 * @author munterfi
 */
@FunctionalInterface
public interface ResultSink<T extends Pipe> {

    /**
     * Exports the data.
     *
     * @param pipe The data transport to be processed.
     */
    void process(T pipe);
}
