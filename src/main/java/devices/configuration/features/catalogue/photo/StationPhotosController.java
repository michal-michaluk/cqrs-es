package devices.configuration.features.catalogue.photo;

import devices.configuration.features.FileSizeValidator;
import devices.configuration.features.catalogue.exceptions.ErrorMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;

import static devices.configuration.features.catalogue.StationException.photoNotFound;
import static java.util.UUID.fromString;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.status;

@RestController
@RequiredArgsConstructor
public class StationPhotosController {

    private final StationPhotoService stationPhotoService;

    @Value("${station.photo.sizeLimitInMb:10}")
    private int photoSizeLimit;

    @PostMapping({
            "/installation/stations/{stationName}/photos",
            "/pub/stations/{stationName}/photos"
    })
    public ResponseEntity<?> addPhoto(
            @PathVariable String stationName,
            @RequestPart("photo") MultipartFile photo,
            AddPhotoRequest addPhotoRequest) {
        var sizeError = validate(photo);
        if (sizeError.isPresent()) {
            return badRequest().body(sizeError.get());
        }

        PhotoResponse response = stationPhotoService.store(stationName, photo, addPhotoRequest);
        return status(CREATED).body(response);
    }

    @DeleteMapping({
            "/installation/stations/{stationName}/photos/{photoId}",
            "/pub/stations/{stationName}/photos/{photoId}"
    })
    public void removePhoto(@PathVariable String stationName, @PathVariable String photoId) {
        stationPhotoService.remove(stationName, createUuid(stationName, photoId));
    }

    private Optional<ErrorMessage> validate(MultipartFile photo) {
        return new FileSizeValidator(photoSizeLimit).validate(photo);
    }

    private UUID createUuid(String stationName, String photoId) {
        UUID uuid;
        try {
            uuid = fromString(photoId);
        } catch (IllegalArgumentException e) {
            throw photoNotFound(stationName, photoId);
        }
        return uuid;
    }

}
