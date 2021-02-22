package devices.configuration.features.images;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.Test;

import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;

class ImageThumbnailFactoryTest {

    @Test
    void Should_create_thumbnail_from_image() throws IOException {
        //given
        byte[] image = Files.readAllBytes(Path.of("./src/test/resources/", "station.jpeg"));
        ImageThumbnailFactory imageThumbnailFactory = new ImageThumbnailFactory(100, 100);
        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setFileName("some-file");
        imageEntity.setFileType("png");
        imageEntity.setId(randomUUID().toString());
        imageEntity.setData(image);

        //when
        ImageThumbnail thumbnail = imageThumbnailFactory.create(imageEntity);

        //then
        assertThat(thumbnail.getFileName()).isEqualTo(imageEntity.getFileName());
        assertThumbnailCorrectSize(thumbnail, 100, 100);
    }

    private void assertThumbnailCorrectSize(ImageThumbnail thumbnail, int height, int width) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(thumbnail.getImage()));
        assertThat(bufferedImage.getHeight()).isEqualTo(height);
        assertThat(bufferedImage.getWidth()).isEqualTo(width);
    }
}
