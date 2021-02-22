package devices.configuration.features.images;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
class ImageThumbnailFactory {

    private final int height;
    private final int width;

    public ImageThumbnailFactory(@Value("${features.images.thumbnail.height}") int height, @Value("${features.images.thumbnail.width}") int width) {
        this.height = height;
        this.width = width;
    }

    ImageThumbnail create(ImageEntity imageEntity) {
        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageEntity.getData()));
            image = resize(image);
            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                ImageIO.write(image, "png", byteArrayOutputStream);
                byteArrayOutputStream.flush();
                byte[] imageInBytes = byteArrayOutputStream.toByteArray();
                byteArrayOutputStream.close();
                return new ImageThumbnail(imageEntity.getFileName(), imageInBytes);
            }
        } catch (IOException ioException) {
            throw new CannotCreateThumbnailFromImage(ioException);
        }
    }

    private BufferedImage resize(BufferedImage img) {
        Image tmp = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return resized;
    }
}
