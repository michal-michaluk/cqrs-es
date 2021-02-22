package devices.configuration.features.images;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/installation/images")
class ImagesController {

    private final ImagesStorage stationImagesStorage;

    @GetMapping("/{imageId}")
    ResponseEntity<Resource> getImage(
            @PathVariable String imageId,
            @RequestParam(value = "thumbnail", defaultValue = "false", required = false) boolean thumbnail) {
        return thumbnail ? returnThumbnail(imageId) : returnFullSizeImage(imageId);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    ImagesListResponse getImagesIds() {
        List<String> ids = stationImagesStorage.getImagesIds();
        ImagesListResponse imagesListResponse = new ImagesListResponse();
        imagesListResponse.setImagesIds(ids);
        return imagesListResponse;
    }

    @DeleteMapping("/{imageId}")
    void deleteImage(@PathVariable String imageId) {
        stationImagesStorage.delete(ImageId.of(imageId));
    }

    private ResponseEntity<Resource> returnFullSizeImage(@PathVariable String imageId) {
        ImageEntity imageEntity = stationImagesStorage.getImage(ImageId.of(imageId));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=\"" + imageEntity.getFileName() + "\"")
                .header(HttpHeaders.CONTENT_TYPE, imageEntity.getFileType())
                .body(new ByteArrayResource(imageEntity.getData()));
    }

    private ResponseEntity<Resource> returnThumbnail(@PathVariable String imageId) {
        ImageThumbnail imageThumbnail = stationImagesStorage.getImageThumbnail(ImageId.of(imageId));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=\"" + imageThumbnail.getFileName() + "\"")
                .header(HttpHeaders.CONTENT_TYPE, "image/png")
                .body(new ByteArrayResource(imageThumbnail.getImage()));
    }
}
