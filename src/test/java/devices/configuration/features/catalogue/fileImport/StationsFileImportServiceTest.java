package devices.configuration.features.catalogue.fileImport;

import devices.configuration.features.catalogue.StationFactory;
import devices.configuration.features.catalogue.StationImageProperties;
import devices.configuration.features.catalogue.StationsRepository;
import devices.configuration.features.catalogue.fileImport.csc.StationInstallationToCscResponse;
import devices.configuration.features.catalogue.fileImport.csc.StationsFileImportToCscService;
import devices.configuration.features.catalogue.fileImport.csc.UploadStationsToCscCommand;
import devices.configuration.features.images.ImageId;
import devices.configuration.features.images.ImagesStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class StationsFileImportServiceTest {

    private StationsFileImportToCscService stationsService;
    private ImagesStorage imagesStorage;
    private StationImageProperties imageProperties;

    @BeforeEach
    void setupSystemUnderTest() {
        StationsRepository stationsRepository = mock(StationsRepository.class);
        imagesStorage = mock(ImagesStorage.class);
        when(imagesStorage.storeImage(any(MultipartFile.class))).thenReturn(ImageId.of(UUID.randomUUID().toString()));
        StationImageProperties stationImageProperties = new StationImageProperties();
        stationImageProperties.setId(UUID.randomUUID().toString());
        imageProperties = spy(stationImageProperties);
        stationsService = new StationsFileImportToCscService(stationsRepository, imagesStorage, new StationFactory(imageProperties));
    }

    @Test
    void Should_upload_stations_with_new_image() throws Exception {
        //given
        UploadStationsToCscCommand uploadStationsCommand = new UploadStationsToCscCommand.Builder(getTestFile())
                .withImage(getTestImage())
                .build();

        //when
        StationInstallationToCscResponse stationInstallResponse = stationsService.uploadStations(uploadStationsCommand);

        //then
        assertThat(stationInstallResponse.getInstalled()).isEqualTo(4);
        verify(imagesStorage).storeImage(any(MultipartFile.class));
    }

    @Test
    void Should_upload_stations_with_new_already_existing_image() throws Exception {
        //given
        UploadStationsToCscCommand uploadStationsCommand = new UploadStationsToCscCommand.Builder(getTestFile())
                .withImageReference(UUID.randomUUID().toString())
                .build();

        //when
        StationInstallationToCscResponse stationInstallResponse = stationsService.uploadStations(uploadStationsCommand);

        //then
        assertThat(stationInstallResponse.getInstalled()).isEqualTo(4);
        verifyNoInteractions(imagesStorage);
    }

    @Test
    void Should_upload_stations_without_image() throws Exception {
        //given
        UploadStationsToCscCommand uploadStationsCommand = new UploadStationsToCscCommand.Builder(getTestFile())
                .build();

        //when
        StationInstallationToCscResponse stationInstallResponse = stationsService.uploadStations(uploadStationsCommand);

        //then
        assertThat(stationInstallResponse.getInstalled()).isEqualTo(4);
        verifyNoInteractions(imagesStorage);
        verify(imageProperties, times(4)).getId();
    }

    private MultipartFile getTestFile() throws IOException {
        return new MockMultipartFile("stations.csv", new FileSystemResource("./src/test/resources/stations.csv").getInputStream());
    }

    private MultipartFile getTestImage() throws IOException {
        return new MockMultipartFile("station.jpg", new FileSystemResource("./src/test/resources/station.jpeg").getInputStream());
    }
}