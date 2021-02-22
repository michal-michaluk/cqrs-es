package devices.configuration.features.images;

import com.jayway.jsonpath.JsonPath;
import devices.configuration.IntegrationTest;
import devices.configuration.SecurityFixture;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;

@IntegrationTest
class ImageControllerIntegrationTest {

    @Autowired
    TestRestTemplate testRestTemplate;

    @Autowired
    ImagesStorage stationImagesStorage;

    @Autowired
    SecurityFixture securityFixture;

    @Test
    void Should_get_image() {
        // given
        URI imageLocation = storeImage();

        // when
        ResponseEntity<byte[]> response = testRestTemplate.exchange(imageLocation, GET, new HttpEntity<>(getMultipartFormHeaders()), byte[].class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThatImageSizeIsOk(response.getBody(), 194, 259);
    }

    @Test
    void Uploaded_image_should_be_on_images_list() {
        // given
        URI imageLocation = storeImage();

        // when
        ResponseEntity<String> response = testRestTemplate.exchange("/installation/images", GET, new HttpEntity<>(getApplicationJsonHeaders()), String.class);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(JsonPath.<List<String>>read(response.getBody(), "$.imagesIds[*]").contains(getImageId(imageLocation)));
    }

    @Test
    void Should_get_image_thumbnail() {
        URI imageLocation = storeImage();

        // when
        ResponseEntity<byte[]> response = testRestTemplate.exchange(imageLocation + "?thumbnail=true", GET, new HttpEntity<>(getApplicationJsonHeaders()), byte[].class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThatImageSizeIsOk(response.getBody(), 100, 100);
    }

    @Test
    void Should_delete_image() {
        // given
        URI imageLocation = storeImage();
        ResponseEntity<byte[]> uploadImageResponse = testRestTemplate.exchange(imageLocation, GET, new HttpEntity<>(getMultipartFormHeaders()), byte[].class);
        assertThat(uploadImageResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        //when
        testRestTemplate.exchange(imageLocation, DELETE, new HttpEntity<>(getApplicationJsonHeaders()), Void.class);

        //then
        ResponseEntity<String> response = testRestTemplate.exchange(imageLocation, GET, new HttpEntity<>(getApplicationJsonHeaders()), String.class);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }


    private String getImageId(URI imageLocation) {
        String[] split = imageLocation.getPath().split("/");
        return split[split.length - 1];
    }

    private MultiValueMap<String, String> getApplicationJsonHeaders() {
        MultiValueMap<String, String> headers;
        headers = new LinkedMultiValueMap<>();
        headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        headers.add(HttpHeaders.AUTHORIZATION, securityFixture.operatorToken());
        return headers;
    }

    private MultiValueMap<String, String> getMultipartFormHeaders() {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add(HttpHeaders.ACCEPT, MediaType.MULTIPART_FORM_DATA_VALUE);
        headers.add(HttpHeaders.AUTHORIZATION, securityFixture.operatorToken());
        return headers;
    }

    private URI storeImage() {
        ImageId imageId = stationImagesStorage.storeImage(getImageMultipartFile());

        return ServletUriComponentsBuilder.fromPath("/installation/images/{imageId}")
                .buildAndExpand(imageId.getId()).toUri();
    }

    private MockMultipartFile getImageMultipartFile() {
        File file = new FileSystemResource("./src/test/resources/station.jpeg").getFile();
        try {
            return new MockMultipartFile("abc", "abc", MediaType.IMAGE_JPEG_VALUE, new FileInputStream(file));
        } catch (IOException e) {
            fail("Image reading during test setup failed");
            return null;
        }
    }

    private void assertThatImageSizeIsOk(byte[] image, int width, int height) {
        BufferedImage bufferedImage;
        try {
            bufferedImage = ImageIO.read(new ByteArrayInputStream(image));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertThat(bufferedImage.getWidth()).isEqualTo(width);
        assertThat(bufferedImage.getHeight()).isEqualTo(height);
    }
}
