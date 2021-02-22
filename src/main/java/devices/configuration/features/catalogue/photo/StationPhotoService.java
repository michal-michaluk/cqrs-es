package devices.configuration.features.catalogue.photo;

import devices.configuration.blob.BlobStorage;
import devices.configuration.features.catalogue.Station;
import devices.configuration.features.catalogue.StationsRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;
import java.util.UUID;

import static devices.configuration.features.catalogue.StationException.photoNotFound;
import static devices.configuration.features.catalogue.StationException.stationNotFound;

@Service
@AllArgsConstructor
@Slf4j
public class StationPhotoService {
    private StationsRepository stationsRepository;
    private BlobStorage blobStorage;

    PhotoResponse store(String stationName, MultipartFile photo, AddPhotoRequest request) {
        Station station = findStation(stationName);

        URL url = blobStorage.uploadPhoto(photo);

        StationPhoto newPhoto = new StationPhoto()
                .setUrl(url)
                .setName(photo.getOriginalFilename())
                .setCategory(request.getCategory());

        station.addPhoto(newPhoto);
        stationsRepository.save(station);

        return photoResponse(newPhoto);
    }

    public void remove(String stationName, UUID photoId) {
        Station station = findStation(stationName);
        URL url = findPhoto(stationName, photoId, station).getUrl();

        removePhotoFromDb(station, url);

        blobStorage.removePhoto(url);
    }

    @Transactional
    public void removePhotoFromDb(Station station, URL url) {
        station.removePhotos(photo -> photo.getUrl().equals(url));
        stationsRepository.save(station);
    }

    private Station findStation(String stationName) {
        return stationsRepository
                .findByName(stationName)
                .orElseThrow(() -> stationNotFound(stationName));
    }

    private StationPhoto findPhoto(String stationName, UUID photoId, Station station) {
        return station.getPhotos().stream()
                .filter(photo -> photo.getId().equals(photoId))
                .findFirst()
                .orElseThrow(() -> photoNotFound(stationName, photoId));
    }

    private PhotoResponse photoResponse(StationPhoto stationPhoto) {
        return PhotoResponse.builder()
                .id(stationPhoto.getId())
                .url(stationPhoto.getUrl())
                .name(stationPhoto.getName())
                .category(stationPhoto.getCategory())
                .build();
    }
}
