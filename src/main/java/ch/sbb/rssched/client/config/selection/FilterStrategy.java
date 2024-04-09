package ch.sbb.rssched.client.config.selection;

import org.matsim.api.core.v01.Scenario;

/**
 * Defines a strategy for filtering transit lines.
 *
 * @author munterfi
 */
public interface FilterStrategy {

    /**
     * Apply the filter strategy on the scenario.
     *
     * @param scenario the scenario to filter.
     * @return A transit line selection containing the filtered transit routes with their routes.
     */
    TransitLineSelection filter(Scenario scenario);
}