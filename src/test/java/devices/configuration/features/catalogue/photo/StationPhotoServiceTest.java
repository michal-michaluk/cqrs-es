package devices.configuration.features.catalogue.photo;

import devices.configuration.blob.BlobStorage;
import devices.configuration.features.catalogue.Station;
import devices.configuration.features.catalogue.StationsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import static devices.configuration.FileFixture.mockImageWithSize;
import static devices.configuration.features.catalogue.StationsFixture.evb;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

class StationPhotoServiceTest {

    @Mock
    private StationsRepository stationsRepository;

    @Mock
    private BlobStorage blobStorage;

    @InjectMocks
    private StationPhotoService photoService;

    @Captor
    private ArgumentCaptor<Station> captor;

    private static final UUID PHOTO_ID = randomUUID();

    @BeforeEach
    void setup() {
        initMocks(this);

        blobWorks();
    }

    @Test
    void Should_store_photo() {
        // given
        MockMultipartFile photo = mockImageWithSize(3);
        Station station = stationExistsWithImageId(PHOTO_ID);
        String category = "someCategory";

        // when
        photoService.store(station.getName(), photo, new AddPhotoRequest(category));

        // then
        StationPhoto stationPhoto = captureStation()
                .getPhotos().stream()
                .filter(p -> p.getName().equals(photo.getOriginalFilename()))
                .findFirst()
                .orElseThrow();

        assertThat(stationPhoto.getCategory()).isEqualTo(category);
        assertThat(stationPhoto.getUrl()).isEqualTo(url());
    }

    @Test
    void Should_remove_photo() {
        // when
        Station station = stationExistsWithImageId(PHOTO_ID);
        photoService.remove(station.getName(), PHOTO_ID);
        photoRemoved(PHOTO_ID);

    }

    @Test
    void Should_throw_for_storing_photo_of_non_existing_station() {
        // given
        stationDoesntExist();

        // expected
        assertThrows(ResponseStatusException.class,
                () -> photoService.store("station", mockImageWithSize(3), new AddPhotoRequest("any")));
        nothingSavedInDB();
    }

    @Test
    void Should_throw_for_deleting_photo_of_non_existing_station() {
        // given
        stationDoesntExist();

        // expected
        assertThrows(ResponseStatusException.class,
                () -> photoService.remove("station", PHOTO_ID));
        nothingSavedInDB();
    }

    @Test
    void Should_throw_for_deleting_photo_from_wrong_station() {
        // given
        UUID photoId = randomUUID();
        UUID photoId2 = randomUUID();
        Station station = stationExistsWithImageId(photoId);
        stationExistsWithImageId(photoId2);

        // expected
        assertThrows(ResponseStatusException.class,
                () -> photoService.remove(station.getName(), photoId2));
        nothingSavedInDB();
    }

    @Test
    void Should_throw_and_not_save_in_DB_for_exception_thrown_by_blob_during_upload() {
        // given
        Station station = stationExistsWithImageId(PHOTO_ID);
        blobThrows();

        // expected
        assertThrows(ResponseStatusException.class,
                () -> photoService.store(station.getName(), mockImageWithSize(3), new AddPhotoRequest("any")));
        nothingSavedInDB();
    }

    @Test
    void Should_throw_and_save_in_DB_for_exception_thrown_by_blob_during_removal() {
        // given
        Station station = stationExistsWithImageId(PHOTO_ID);
        blobThrows();

        // expected
        assertThrows(ResponseStatusException.class, () -> photoService.remove(station.getName(), PHOTO_ID));
        photoRemoved(PHOTO_ID);
    }

    private void blobWorks() {
        doReturn(url()).when(blobStorage).uploadPhoto(any());
    }

    private void blobThrows() {
        doThrow(new ResponseStatusException(INTERNAL_SERVER_ERROR, "any")).when(blobStorage).uploadPhoto(any());
        doThrow(new ResponseStatusException(INTERNAL_SERVER_ERROR, "any")).when(blobStorage).removePhoto(any());
    }

    private Station stationExistsWithImageId(UUID photoId) {
        Station station = evb();

        try {
            station.getPhotos().add(new StationPhoto()
                    .setName("somename.jpg")
                    .setCategory("entrance")
                    .setUrl(new URL("http://example"))
                    .setId(photoId));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


        doReturn(of(station))
                .when(stationsRepository).findByName(station.getName());

        return station;
    }

    private void stationDoesntExist() {
        doReturn(empty()).when(stationsRepository).findByName(any());
    }

    private URL url() {
        try {
            return new URL("http://example");
        } catch (MalformedURLException e) {
            return null;
        }
    }

    private Station captureStation() {
        verify(stationsRepository).save(captor.capture());
        return captor.getValue();
    }

    private void photoRemoved(UUID photoId) {
        // then
        assertTrue(
                captureStation()
                        .getPhotos().stream()
                        .noneMatch(p -> p.getId().equals(photoId))
        );
    }

    private void nothingSavedInDB() {
        verify(stationsRepository, never()).save(any());
    }
}