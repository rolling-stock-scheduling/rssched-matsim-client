package ch.sbb.rssched.client.pipeline.scenario;

import ch.sbb.rssched.client.config.selection.FilterStrategy;
import ch.sbb.rssched.client.config.selection.TransitLineSelector;
import ch.sbb.rssched.client.pipeline.core.Filter;

/**
 * Filters transit line IDs of interest based on strategy.
 *
 * @author munterfi
 */
class TransitLineFilter implements Filter<ScenarioPipe> {
    private final FilterStrategy filterStrategy;

    /**
     * Constructs a TransitLineFilter with the specified filter strategy.
     *
     * @param filterStrategy the filter strategy to apply.
     */
    public TransitLineFilter(FilterStrategy filterStrategy) {
        this.filterStrategy = filterStrategy;
    }

    @Override
    public void apply(ScenarioPipe pipe) {
        pipe.selection = new TransitLineSelector(filterStrategy, pipe.scenario).get();
    }
}
