package devices.configuration.maintenance;

import com.jayway.jsonpath.JsonPath;
import devices.configuration.DisableAuthorizationConfiguration;
import devices.configuration.legacy.stationImport.StationFromEggplantImportService;
import devices.configuration.legacy.stationImport.StationFromEggplantImportService.ImportMode;
import devices.configuration.legacy.stationImport.StationsImporter.UpdateScope;
import devices.configuration.legacy.stationImport.report.ImportReport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TriggerImportController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(DisableAuthorizationConfiguration.class)
public class TriggerImportControllerMvcTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private StationFromEggplantImportService importService;

    @Test
    public void Should_return_201_and_report() throws Exception {
        // given
        ImportReport report = ImportReport.blank()
                .setMessage("sth went wrong");
        report.addRejectedLocation("EVB-01", "shit happened");
        report.addUpdated();
        report.addCreated();
        report.addUnchanged();

        report.addSucceededPage();
        report.addFailedPage(1, "503 returned");

        doReturn(report).when(importService).importStations(ImportMode.TEST, Set.of(UpdateScope.values()));

        // when
        var response = mockMvc.perform(
                MockMvcRequestBuilders.post("/prv/legacy/import/stations?mode=TEST&update=LOCATION,CONNECTORS,OWNERSHIP,SETTINGS&apiKey=RR:d65d297a-427a-476e-87e1-b723c8b0c5c8")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();

        // then
        verify(importService).importStations(ImportMode.TEST, Set.of(UpdateScope.values()));

        assertThat(JsonPath.<Integer>read(response, "$.all")).isEqualTo(4);
        assertThat(JsonPath.<Integer>read(response, "$.rejected")).isEqualTo(1);
        assertThat(JsonPath.<Integer>read(response, "$.updated")).isEqualTo(1);
        assertThat(JsonPath.<Integer>read(response, "$.created")).isEqualTo(1);
        assertThat(JsonPath.<List<String>>read(response, "$.rejectedStations")).hasSize(0);

        assertThat(JsonPath.<Integer>read(response, "$.pagesSummary.all")).isEqualTo(2);
        assertThat(JsonPath.<Integer>read(response, "$.pagesSummary.failed")).isEqualTo(1);
        assertThat(JsonPath.<Integer>read(response, "$.pagesSummary.succeeded")).isEqualTo(1);
        assertThat(JsonPath.<List<String>>read(response, "$.pagesSummary.failedPages")).hasSize(1);
    }
}
