package devices.configuration.legacy.stationImport;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

class StationsImporterParseTest {

    ObjectMapper mapper = new ObjectMapper();

    @Test
    void parseSingleStationWithLcpOwnerAndTwoPoints() throws IOException {
        var json = mapper.readTree(new ClassPathResource("./eggplant-stations-export.json").getFile())
                .get(0).toPrettyString();

        var elements = mapper.readValue(json, StationView.ChargingStation.class);
        Assertions.assertThat(elements).isNotNull();
    }
}