package ch.sbb.rssched.client.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;

class RsschedRequestConfigReaderIT {

    private RsschedRequestConfigReader reader;

    @BeforeEach
    void setUp() {
        reader = new RsschedRequestConfigReader();
    }

    @Test
    void shouldReadConfigFile() throws IOException {
        URL filePath = getClass().getResource("/ch/sbb/rssched/client/config/config.xlsx");
        assert filePath != null;
        RsschedRequestConfig config = reader.readExcelFile(filePath.getPath());
        System.out.println(config);
    }

}
