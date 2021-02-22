package devices.configuration.legacy.stationImport;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import devices.configuration.JsonConfiguration;
import devices.configuration.legacy.stationImport.report.ImportReport;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import static org.assertj.core.api.Assertions.assertThat;

public class StationPageDeserializerTest {
    ObjectMapper mapper = JsonConfiguration.OBJECT_MAPPER;

    @Test
    public void Should_deserialize_page_JSON_properly() throws Exception {
        // given
        String json = "{\n" +
                "    \"content\": [" + loadExample() + "],\n" +
                "    \"first\": true,\n" +
                "    \"totalElements\": 1,\n" +
                "    \"size\": 1,\n" +
                "    \"number\": 0\n" +
                "}";

        // when
        var result = mapper.readValue(json, new TypeReference<RestPage<StationView.ChargingStation>>() {
        });

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getSize()).isEqualTo(1);
        assertThat(result.getNumber()).isEqualTo(0);

        var station = result.getContent().get(0);
        assertThat(station.getName()).isEqualTo("015021");
        assertThat(station.getLocation().getCity()).isEqualTo("Berlin");
        assertThat(station.getLocation().getLongitude()).isEqualTo("13.42681");
        assertThat(station.getLocation().getLatitude()).isEqualTo("52.54529");
    }

    @Test
    public void Should_serialize_import_report_in_pretty_format() throws JsonProcessingException {
        // given
        ImportReport report = prepareReport();

        // when
        String output = new ObjectMapper()
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(report);

        // then
        assertThat(output).contains("\n");
    }

    private ImportReport prepareReport() {
        ImportReport report = ImportReport.blank()
                .setMessage("sth went wrong");
        report.addRejectedLocation("EVB-01", "shit happened");
        report.addUpdated();
        report.addCreated();
        report.addUnchanged();

        report.addSucceededPage();
        report.addFailedPage(1, "503 returned");
        return report;
    }

    private String loadExample() throws Exception {
        return mapper.readTree(new ClassPathResource("./eggplant-stations-export.json").getFile())
                .get(0).toPrettyString();
    }
}
