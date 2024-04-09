package ch.sbb.rssched.client.pipeline.passenger;

import ch.sbb.rssched.client.config.selection.FilterStrategy;
import ch.sbb.rssched.client.config.selection.TransitLineSelector;
import ch.sbb.rssched.client.pipeline.core.Filter;

/**
 * Filters transit line IDs of interest based on strategy.
 *
 * @author munterfi
 */
class TransitLineFilter implements Filter<PassengerPipe> {
    private final FilterStrategy filterStrategy;

    public TransitLineFilter(FilterStrategy filterStrategy) {
        this.filterStrategy = filterStrategy;
    }

    @Override
    public void apply(PassengerPipe pipe) {
        pipe.transitLineIds().addAll(new TransitLineSelector(filterStrategy, pipe.scenario()).get().getLineIds());
    }
}
