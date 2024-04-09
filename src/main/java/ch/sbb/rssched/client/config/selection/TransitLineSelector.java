package ch.sbb.rssched.client.config.selection;

import org.matsim.api.core.v01.Scenario;

/**
 * Applies filter strategy on scenario.
 *
 * @author munterfi
 */
public class TransitLineSelector {

    private final TransitLineSelection selection;

    /**
     * Constructs a TransitLineSelection with the specified filter strategy.
     *
     * @param strategy the filter strategy to apply.
     * @param scenario the scenario to apply the filter to.
     */
    public TransitLineSelector(FilterStrategy strategy, Scenario scenario) {
        this.selection = strategy.filter(scenario);
    }

    public TransitLineSelection get() {
        return selection;
    }
}
