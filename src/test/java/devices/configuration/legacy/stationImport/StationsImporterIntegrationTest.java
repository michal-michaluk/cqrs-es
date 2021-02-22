package devices.configuration.legacy.stationImport;

import devices.configuration.IntegrationTest;
import devices.configuration.features.catalogue.*;
import devices.configuration.legacy.stationImport.StationsImporter.UpdateScope;
import devices.configuration.legacy.stationImport.report.ImportReport;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@IntegrationTest
class StationsImporterIntegrationTest {

    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    StationsImporter importer;

    @Autowired
    StationsRepository repository;

    @Test
    void importAllExamples() throws IOException {
        List<StationView.ChargingStation> stations = List.of(mapper.readValue(new ClassPathResource("./eggplant-stations-export.json").getFile(), StationView.ChargingStation[].class));
        importer.importStations(Set.of(UpdateScope.values()), stations, new ImportReport());

        Station station = repository.findByName("015021").orElseThrow();
        Assertions.assertThat(station.getCpo()).isEqualTo("Vattenfall DE");
        Assertions.assertThat(station.getLcp()).isEqualTo("LCP vattenfall-de");
        Assertions.assertThat(station.getSettings())
                .isEqualTo(Settings.builder()
                        .autoStart(false)
                        .remoteControl(true)
                        .billing(true)
                        .reimbursement(true)
                        .showOnMap(true)
                        .publicAccess(true)
                        .build()
                );

        var connectors = station.getConnectors().stream()
                .sorted(Comparator.comparing(Connector::getOcppConnectorId))
                .map(connector -> Map.of(
                        "name", orDash(connector.getName()),
                        "ocppConnectorId", orDash(connector.getOcppConnectorId()),
                        "evseId", orDash(connector.getEvseId()),
                        "type", orDash(connector.getType()),
                        "format", orDash(connector.getFormat())
                ))
                .collect(Collectors.toList());

        Assertions.assertThat(connectors).containsExactly(
                Map.of(
                        "evseId", "DE*VAT*E*ME015021*R",
                        "format", Format.SOCKET,
                        "name", "015021_R",
                        "ocppConnectorId", 1,
                        "type", ConnectorType.TYPE_2_MENNEKES),
                Map.of(
                        "evseId", "DE*VAT*E*ME015021*L",
                        "format", Format.SOCKET,
                        "name", "015021_L",
                        "ocppConnectorId", 2,
                        "type", ConnectorType.TYPE_2_MENNEKES)
        );
    }

    private Object orDash(Object value) {
        return value == null ? " - " : value;
    }
}
