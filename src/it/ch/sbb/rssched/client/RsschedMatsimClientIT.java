package ch.sbb.rssched.client;

import ch.sbb.rssched.client.config.RsschedRequestConfig;
import ch.sbb.rssched.client.config.selection.VehicleTypeFilterStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Set;

import static ch.sbb.rssched.client.IntegrationTestData.*;

class RsschedMatsimClientIT {

    @BeforeEach
    void setUp() throws IOException {
        new IntegrationTestData(false).setup();
    }

    @Test
    void testProcess() {
        VehicleTypeFilterStrategy filterStrategy = new VehicleTypeFilterStrategy(
                Set.of(new VehicleTypeFilterStrategy.VehicleCategory("Rail", Set.of("RE_RB_veh_type"))));
        RsschedRequestConfig config = RsschedRequestConfig.builder().setInputDirectory(IT_INPUT_DIRECTORY)
                .setOutputDirectory(IT_OUTPUT_DIRECTORY).setRunId(RUN_ID).setFilterStrategy(filterStrategy)
                .buildWithDefaults();

        RsschedMatsimClient client = new RsschedMatsimClient("localhost", 3000);
        client.process(config);
    }
}