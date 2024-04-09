package ch.sbb.rssched.client.dto.request;

/**
 * @param forbidDeadHeadTrips default is false, which means DeadHeadTrips are allowed.
 * @param dayLimitThreshold   duration in sec; vehicles with stopping times under this threshold do not count into
 *                            dayLimit at stations. Default: 0
 * @param shunting            the shunting configuration.
 * @param maintenance         the maintenance configuration.
 * @param costs               the cost configuration.
 */
record Config(boolean forbidDeadHeadTrips, int dayLimitThreshold, ShuntingConfig shunting,
              MaintenanceConfig maintenance, CostConfig costs) {
    Config {
        if (dayLimitThreshold < 0) {
            throw new IllegalArgumentException("Day limit threshold must be non-negative.");
        }
    }
}
