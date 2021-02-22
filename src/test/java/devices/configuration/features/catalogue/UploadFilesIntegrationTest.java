package devices.configuration.features.catalogue;

import com.jayway.jsonpath.JsonPath;
import devices.configuration.IntegrationTest;
import devices.configuration.SecurityFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.UUID;

import static devices.configuration.FileFixture.imageFile;
import static devices.configuration.features.catalogue.StationsResponseAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
class UploadFilesIntegrationTest {

    @Autowired
    StationsRepository stationRepository;

    @Autowired
    RequestFixture requestFixture;

    @Autowired
    SecurityFixture securityFixture;

    @BeforeEach
    void setUp() {
        stationRepository.deleteAll();
    }

    @Test
    void Should_return_OK_and_number_of_wrong_lines_when_file_is_invalid() {
        //when upload invalid file
        ResponseEntity<String> response = requestFixture.importStations(getInvalidTestFile(), imageFile());

        //then
        assertThat(response).isCreated()
                .hasStationInstallationResponseAsBody(
                        2, 0, 4
                );
    }

    @Test
    void Should_upload_the_same_stations_by_station_name_only_once() {
        // given
        importStations(getTestFile());

        // when
        importStations(getTestFile());

        // then
        ResponseEntity<String> stationsListResponse = requestFixture.getStations();
        resultIsTheSameAsDataInFile(stationsListResponse);
    }

    @Test
    void Should_update_station_during_import_when_station_id_is_filled() throws IOException {
        // given
        importStations(getUpdateTestFile());

        // when
        importStations(getFileForUpdateWithNewStationName("newNameOfStation"));

        // then
        ResponseEntity<String> stationsListResponse = requestFixture.getStations();
        assertThat(stationsListResponse)
                .hasStationWithNameInBody("newNameOfStation", station -> station
                        .hasPhysicalReference("EVB-4572563")
                        .hasNumberOfOutlets(2)
                        .hasMaxNumberOfOutlets(20)
                        .hasVendor("EVBOX")
                        .hasProduct("ELVI")
                        .hasProductDetails("ELVI273")
                        .hasColor("Gray")
                        .hasComment1("looks like a shoebox")
                        .hasComment2("just some free fields plhed")
                        .hasCapabilitiesAvailableOcppVersions("1.5/1.6")
                        .hasCapabilitiesDebitPayment(false)
                        .hasCapabilitiesReservation(true)
                        .hasCapabilitiesCreditPayment(false)
                        .hasCapabilitiesRfidReader(true)
                        .hasCapabilitiesRemoteStart(true)
                        .hasCapabilitiesScnDlb(true)
                        .hasCapabilitiesTokenGrouping(true)
                        .hasCapabilitiesSmartCharging(true)
                        .hasCapabilitiesUnlock(true)
                        .hasCapabilitiesAc(true)
                        .hasCapabilitiesDc(false)
                );
    }

    @Test
    void Should_export_have_the_same_number_of_lines_as_imported_file() throws IOException {
        // given
        importStations(getTestFile());

        // when
        ResponseEntity<String> response = requestFixture.exportStations();

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(numberOfLines(response.getBody())).isEqualTo(numberOfLines(getTextFromFile(getTestFile())));
    }

    @Test
    void Should_return_400_when_image_is_in_disallowed_format() {
        // when
        ResponseEntity<String> response = requestFixture.importStations(getTestFile(), getPhotoFileOfForbiddenType());

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void Should_not_import_station_with_unknown_connector_type() {
        // given
        importStations(getTestFile("stations_connector_types.csv"));

        // when
        ResponseEntity<String> response = requestFixture.getStations();

        // then
        StationsResponseAssert.assertThat(response)
                .hasNoStationWithNameInBody("UNKNOWN-CONNECTOR-TYPE")
                .hasStationWithNameInBody("CORRECT-CONNECTOR-TYPE", station ->
                        station.hasConnectors(new Connector()
                                .setName("CORRECT-CONNECTOR-TYPE_1")
                                .setType(ConnectorType.CHADEMO)
                                .setPhysicalReference("AB3459")
                                .setEvseId("AB34592")
                                .setFormat(Format.CABLE)
                                .setVoltage("230")
                                .setPhases("1")
                                .setAmps("32")
                                .setAc(true)
                                .setDc(false)));
    }

    private String getTextFromFile(FileSystemResource file) throws IOException {
        return StreamUtils.copyToString(file.getInputStream(), Charset.defaultCharset());
    }

    private int numberOfLines(String text) {
        return text.split("\r\n|\r|\n").length;
    }

    private ResponseEntity<String> importStations(FileSystemResource file) {
        ResponseEntity<String> response = requestFixture.importStations(file, imageFile());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return response;
    }

    private void resultIsTheSameAsDataInFile(ResponseEntity<String> stationsListResponse) {
        assertThat(stationsListResponse).isOK()
                .hasStationWithNameInBody("EVB-123234", station -> station
                        .hasPhysicalReference("EVB-123234")
                        .hasNumberOfOutlets(2)
                        .hasMaxNumberOfOutlets(20)
                        .hasVendor("EVBOX")
                        .hasProduct("ELVI")
                        .hasProductDetails("ELVI273")
                        .hasColor("Gray")
                        .hasComment1("looks like a shoebox")
                        .hasComment2("just some free fields plhed")
                        .hasCapabilitiesAvailableOcppVersions("1.5/1.6")
                        .hasCapabilitiesDebitPayment(false)
                        .hasCapabilitiesReservation(true)
                        .hasCapabilitiesCreditPayment(false)
                        .hasCapabilitiesRfidReader(true)
                        .hasCapabilitiesRemoteStart(true)
                        .hasCapabilitiesScnDlb(true)
                        .hasCapabilitiesTokenGrouping(true)
                        .hasCapabilitiesSmartCharging(true)
                        .hasCapabilitiesUnlock(true)
                        .hasCapabilitiesAc(true)
                        .hasCapabilitiesDc(false)
                        .hasConnectors(
                                new Connector()
                                        .setName("EVB-123234_1")
                                        .setPhysicalReference("AB3459")
                                        .setEvseId("AB34592")
                                        .setType(ConnectorType.TYPE_2_MENNEKES)
                                        .setFormat(Format.SOCKET)
                                        .setVoltage("230")
                                        .setPhases("3")
                                        .setAmps("32")
                                        .setAc(true)
                                        .setDc(false),
                                new Connector()
                                        .setName("EVB-123234_2")
                                        .setPhysicalReference("AB3459")
                                        .setEvseId("AB34592")
                                        .setType(ConnectorType.TYPE_2_MENNEKES)
                                        .setFormat(Format.SOCKET)
                                        .setVoltage("230")
                                        .setPhases("3")
                                        .setAmps("32")
                                        .setAc(true)
                                        .setDc(false)
                        )
                        .hasNoLocation()
                )
                .hasStationWithNameInBody("ALF-4532879", station -> station
                        .hasPhysicalReference("ALF-4532879")
                        .hasNumberOfOutlets(2)
                        .hasMaxNumberOfOutlets(255)
                        .hasVendor("ALFEN")
                        .hasProduct("Pro line")
                        .hasProductDetails("Pro line876")
                        .hasColor("White/Gray")
                        .hasComment1("looks like a pile of shit")
                        .hasComment2("palhed")
                        .hasCapabilitiesAvailableOcppVersions("1.2/1.5/1.6/1.6+")
                        .hasCapabilitiesDebitPayment(false)
                        .hasCapabilitiesReservation(true)
                        .hasCapabilitiesCreditPayment(false)
                        .hasCapabilitiesRfidReader(true)
                        .hasCapabilitiesRemoteStart(true)
                        .hasCapabilitiesScnDlb(true)
                        .hasCapabilitiesTokenGrouping(true)
                        .hasCapabilitiesSmartCharging(true)
                        .hasCapabilitiesUnlock(true)
                        .hasCapabilitiesAc(true)
                        .hasCapabilitiesDc(false)
                        .hasConnectors(
                                new Connector()
                                        .setName("ALF-4532879_1")
                                        .setPhysicalReference("AB3459")
                                        .setEvseId("AB34592")
                                        .setType(ConnectorType.TYPE_1_SAE_J1772)
                                        .setFormat(Format.CABLE)
                                        .setVoltage("230")
                                        .setPhases("1")
                                        .setAmps("32")
                                        .setAc(true)
                                        .setDc(false),
                                new Connector()
                                        .setName("ALF-4532879_2")
                                        .setPhysicalReference("AB3458")
                                        .setEvseId("AB34582")
                                        .setType(ConnectorType.TYPE_1_SAE_J1772)
                                        .setFormat(Format.CABLE)
                                        .setVoltage("230")
                                        .setPhases("1")
                                        .setAmps("32")
                                        .setAc(true)
                                        .setDc(false)
                        )
                        .hasNoLocation()
                )
                .hasStationWithNameInBody("GAR-23463424", station -> station
                        .hasPhysicalReference("GAR-23463424")
                        .hasNumberOfOutlets(2)
                        .hasMaxNumberOfOutlets(255)
                        .hasVendor("GARO")
                        .hasProduct("GLB")
                        .hasProductDetails("GLB322")
                        .hasColor("Black")
                        .hasComment1("looks like a dutch wooden shoe")
                        .hasComment2("neekl")
                        .hasCapabilitiesAvailableOcppVersions("2.0")
                        .hasCapabilitiesDebitPayment(false)
                        .hasCapabilitiesReservation(true)
                        .hasCapabilitiesCreditPayment(false)
                        .hasCapabilitiesRfidReader(false)
                        .hasCapabilitiesRemoteStart(false)
                        .hasCapabilitiesScnDlb(false)
                        .hasCapabilitiesTokenGrouping(true)
                        .hasCapabilitiesSmartCharging(true)
                        .hasCapabilitiesUnlock(true)
                        .hasCapabilitiesAc(true)
                        .hasCapabilitiesDc(false)
                        .hasConnectors(
                                new Connector()
                                        .setName("GAR-23463424_1")
                                        .setPhysicalReference("AB3459")
                                        .setEvseId("AB34592")
                                        .setType(ConnectorType.TYPE_2_MENNEKES)
                                        .setFormat(Format.SOCKET)
                                        .setVoltage("230")
                                        .setPhases("3")
                                        .setAmps("32")
                                        .setAc(true)
                                        .setDc(false),
                                new Connector()
                                        .setName("GAR-23463424_2")
                                        .setPhysicalReference("AB3454")
                                        .setEvseId("AB34542")
                                        .setType(ConnectorType.TYPE_2_MENNEKES)
                                        .setFormat(Format.SOCKET)
                                        .setVoltage("230")
                                        .setPhases("3")
                                        .setAmps("32")
                                        .setAc(true)
                                        .setDc(false)
                        )
                        .hasNoLocation()
                )
                .hasStationWithNameInBody("CSR-34564526", station -> station
                        .hasPhysicalReference("CSR-34564526")
                        .hasNumberOfOutlets(1)
                        .hasMaxNumberOfOutlets(255)
                        .hasVendor("CTEK/Chargestorm")
                        .hasProduct("EVA")
                        .hasProductDetails("EVA88")
                        .hasColor("Black/green")
                        .hasComment1("french toast is nice")
                        .hasComment2("akljwhd")
                        .hasCapabilitiesAvailableOcppVersions("1.5")
                        .hasCapabilitiesDebitPayment(false)
                        .hasCapabilitiesReservation(true)
                        .hasCapabilitiesCreditPayment(false)
                        .hasCapabilitiesRfidReader(true)
                        .hasCapabilitiesRemoteStart(true)
                        .hasCapabilitiesScnDlb(true)
                        .hasCapabilitiesTokenGrouping(true)
                        .hasCapabilitiesSmartCharging(true)
                        .hasCapabilitiesUnlock(true)
                        .hasCapabilitiesAc(true)
                        .hasCapabilitiesDc(false)
                        .hasConnectors(
                                new Connector()
                                        .setName("CSR-34564526_1")
                                        .setPhysicalReference("AB3459")
                                        .setEvseId("AB34592")
                                        .setType(ConnectorType.TYPE_1_SAE_J1772)
                                        .setFormat(Format.CABLE)
                                        .setVoltage("230")
                                        .setPhases("1")
                                        .setAmps("32")
                                        .setAc(true)
                                        .setDc(false)
                        )
                        .hasNoLocation()
                );
    }

    private FileSystemResource getTestFile() {
        return new FileSystemResource("./src/test/resources/stations.csv");
    }

    private FileSystemResource getTestFile(String filename) {
        return new FileSystemResource("./src/test/resources/" + filename);
    }

    private FileSystemResource getPhotoFileOfForbiddenType() {
        return new FileSystemResource("./src/test/resources/station.gif");
    }

    private FileSystemResource getInvalidTestFile() {
        return new FileSystemResource("./src/test/resources/wrong_stations.csv");
    }

    private FileSystemResource getUpdateTestFile() {
        return new FileSystemResource("./src/test/resources/station_update.csv");
    }

    private FileSystemResource getFileForUpdateWithNewStationName(String newNameOfStation) throws IOException {
        final UUID stationId = getStationId("EVB-4572563");
        String uploadFileContent = prepareUpdatedStationEntryWithNewName(newNameOfStation, stationId);
        return createFileWithContent(uploadFileContent);
    }

    private String prepareUpdatedStationEntryWithNewName(String newNameOfStation, UUID stationId) throws IOException {
        final String[] lines = getLinesFromFile();
        final String headerLine = lines[0];
        String stationDefinitionLine = lines[1];

        final int firstComaIndex = stationDefinitionLine.indexOf(',');
        final int secondComaIndex = stationDefinitionLine.indexOf(',', firstComaIndex + 1);
        stationDefinitionLine = stationId + stationDefinitionLine.substring(0, firstComaIndex + 1) + newNameOfStation + stationDefinitionLine.substring(secondComaIndex);
        return headerLine + System.lineSeparator() + stationDefinitionLine;
    }

    private FileSystemResource createFileWithContent(String uploadFileContent) throws IOException {
        final FileSystemResource fileForUpdate = new FileSystemResource("./src/test/resources/station_to_update.csv");
        fileForUpdate.getOutputStream().write(uploadFileContent.getBytes());
        fileForUpdate.getFile().deleteOnExit();
        return fileForUpdate;
    }

    private String[] getLinesFromFile() throws IOException {
        final String textFromFile = getTextFromFile(getUpdateTestFile());
        return textFromFile.split("\n", 2);
    }

    private UUID getStationId(String stationName) {
        ResponseEntity<String> station = requestFixture.getStation(stationName);
        return UUID.fromString(JsonPath.read(station.getBody(), "$.id"));
    }
}
