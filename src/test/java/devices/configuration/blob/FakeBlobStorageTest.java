package devices.configuration.blob;

import devices.configuration.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.URL;

import static devices.configuration.FileFixture.imageFile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@IntegrationTest
class FakeBlobStorageTest {

    @Autowired
    BlobStorage blobStorage;

    @Test
    public void Should_upload_file_to_blob_and_remove() throws IOException {
        // given
        MockMultipartFile image = new MockMultipartFile("someName.jpg", imageFile().getInputStream());

        // when
        URL url = blobStorage.uploadPhoto(image);

        // then
        assertThat(url).isNotNull();

        // and when
        blobStorage.removePhoto(url);

        // then no exception
    }

    @Test
    public void Should_throw_when_removing_non_existing_blob() throws IOException {
        // given
        URL non_existing = new URL("http:://stationconfigurationdev.blob.core.windows.net/media/any.jpg");

        // expected
        HttpStatus status = assertThrows(ResponseStatusException.class,
                () -> blobStorage.removePhoto(non_existing)).getStatus();
        assertThat(status).isEqualTo(NOT_FOUND);
    }
}