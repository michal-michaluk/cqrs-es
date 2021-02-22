package devices.configuration.features.catalogue.fileImport;

import devices.configuration.DisableAuthorizationConfiguration;
import devices.configuration.features.catalogue.fileImport.csc.StationInstallationToCscResponse;
import devices.configuration.features.catalogue.fileImport.csc.StationsFileImportToCscService;
import devices.configuration.features.catalogue.fileImport.eggplant.StationsFileImportReport;
import devices.configuration.features.eggplant.EmobilityOldPlatformClient;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.rules.TemporaryFolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static devices.configuration.FileFixture.mockCsvFile;
import static devices.configuration.FileFixture.mockImageWithSize;
import static devices.configuration.features.catalogue.fileImport.StationsFileImportRestController.LEGACY_IMPORT_HEADER;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = StationsFileImportRestController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(DisableAuthorizationConfiguration.class)
class StationsFileImportRestControllerMockMvcTest {

    private int NEW_TEMPLATE_FILE_LENGTH = 2235;
    private int LEGACY_TEMPLATE_FILE_LENGTH = 19652;

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private StationsFileImportToCscService importToCscService;

    @MockBean
    private EmobilityOldPlatformClient oldPlatformClient;

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void Should_return_201_and_list_for_Eggplant_import() throws Exception {
        // given
        StationsFileImportReport report = new StationsFileImportReport(3, asList("one", "two"), emptyList());
        doReturn(report).when(oldPlatformClient).importStationsFile(any(), any(), any());

        // when
        MockHttpServletResponse response = mockMvc.perform(
                multipart("/installation/legacy/stations")
                        .file(mockCsvFile())
                        .param("country", "NORWAY")
                        .param("chargingStationTypeName", "station Type")
                        .param("chargingPointTypeName", "Some point name")
                        .param("cpoName", "HUJ - Nobil")
                        .param("requestor", "superuser")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn().getResponse();

        // then
        assertThat(response.getContentAsString()).contains("one", "two");
    }

    @Test
    public void Should_return_200_for_1MB_station_image_in_CSC_import() throws Exception {
        // given
        doReturn(new StationInstallationToCscResponse()).when(importToCscService).uploadStations(any());
        int correctSize = 1;

        // expect
        mockMvc.perform(
                multipart("/installation/stations")
                        .file(mockCsvFile())
                        .file(mockImageWithSize(correctSize))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    public void Should_return_400_for_too_big_image_in_CSC_import() throws Exception {
        // given
        int tooBigSize = 11;

        // expect
        MvcResult result = mockMvc.perform(
                multipart("/installation/stations")
                        .file(mockCsvFile())
                        .file(mockImageWithSize(tooBigSize))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertThat(result.getResponse().getContentAsString()).contains("exceeded maximum image size");
    }

    @Test
    public void Should_return_new_template_file() throws Exception {
        // given
        MvcResult result = mockMvc.perform(get("/installation/stations-template").accept("text/csv"))
                .andExpect(status().isOk())
                .andReturn();

        // when
        File file = saveTemporaryFile(result);

        // then
        assertProperFileReturned(file, NEW_TEMPLATE_FILE_LENGTH);
    }

    @Test
    public void Should_return_legacy_template_file() throws Exception {
        // given
        HttpHeaders legacyImportHeader = new HttpHeaders();
        legacyImportHeader.add(LEGACY_IMPORT_HEADER, "any");
        MvcResult result = mockMvc.perform(get("/installation/stations-template").accept("text/csv").headers(legacyImportHeader))
                .andExpect(status().isOk())
                .andReturn();

        // when
        File file = saveTemporaryFile(result);

        // then
        assertProperFileReturned(file, LEGACY_TEMPLATE_FILE_LENGTH);
    }

    private void assertProperFileReturned(File file, int legacy_template_file_length) {
        assertThat(file.length() - legacy_template_file_length).isLessThan(5);
    }

    private File saveTemporaryFile(MvcResult result) throws IOException {
        tempFolder.create();
        File file = tempFolder.newFile("downloaded_file");
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(result.getResponse().getContentAsByteArray());
        return file;
    }
}