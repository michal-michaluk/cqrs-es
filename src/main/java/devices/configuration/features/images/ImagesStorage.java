package devices.configuration.features.images;

import devices.configuration.util.Tombstone;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Clock;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

@Component
@RequiredArgsConstructor
public class ImagesStorage {

    private final ImagesRepository imagesRepository;
    private final ImageThumbnailFactory imageThumbnailFactory;
    private final Clock clock;

    public ImageId storeImage(MultipartFile file) {
        String fileName = StringUtils.cleanPath(requireNonNull(file.getOriginalFilename()));
        ImageValidator.validate(file);

        try {
            if (fileName.contains("..")) {
                throw new IllegalArgumentException("Filename contains invalid path sequence " + fileName + ".");
            }
            ImageEntity dbFile = new ImageEntity();
            dbFile.setFileType(file.getContentType());
            dbFile.setFileName(fileName);
            dbFile.setData(file.getBytes());
            return ImageId.of(imagesRepository.save(dbFile).getId());
        } catch (IOException ex) {
            throw new ImageStorageFailed(fileName, ex);
        }
    }

    @Transactional(readOnly = true)
    public ImageThumbnail getImageThumbnail(ImageId imageId) {
        ImageEntity imageEntity = imagesRepository.findByIdAndTombstoneIsNull(imageId.getId())
                .orElseThrow(() -> new CouldNotFindImageWithGivenId(imageId));
        return imageThumbnailFactory.create(imageEntity);
    }

    @Transactional(readOnly = true)
    public List<String> getImagesIds() {
        return imagesRepository.findAllProjectedByAndTombstoneIsNull().stream().map(ImageEntityIdProjection::getId).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ImageEntity getImage(ImageId imageId) {
        return imagesRepository.findByIdAndTombstoneIsNull(imageId.getId()).orElseThrow(() -> new CouldNotFindImageWithGivenId(imageId));
    }

    @Transactional
    public void delete(ImageId imageId) {
        ImageEntity imageEntity = imagesRepository.findByIdAndTombstoneIsNull(imageId.getId())
                .orElseThrow(() -> new CouldNotFindImageWithGivenId(imageId));
        imageEntity.setTombstone(Tombstone.atTime(clock));
        imagesRepository.save(imageEntity);
    }
}
