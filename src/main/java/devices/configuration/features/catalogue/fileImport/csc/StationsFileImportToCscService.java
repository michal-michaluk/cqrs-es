package devices.configuration.features.catalogue.fileImport.csc;

import devices.configuration.features.catalogue.Station;
import devices.configuration.features.catalogue.StationFactory;
import devices.configuration.features.catalogue.StationsRepository;
import devices.configuration.features.catalogue.fileImport.csc.StationInstallationToCscResponse.LineImportResult;
import devices.configuration.features.images.ImageId;
import devices.configuration.features.images.ImagesStorage;
import devices.configuration.util.CsvReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class StationsFileImportToCscService {
    private final StationsRepository stationsRepository;
    private final ImagesStorage imagesStorage;
    private final StationFactory stationFactory;

    public StationInstallationToCscResponse uploadStations(UploadStationsToCscCommand uploadStationsCommand) throws IOException {
        StationInstallationToCscResponse stationInstallationResponse = new StationInstallationToCscResponse();
        Optional<ImageId> imageId = getImageIdFromUsersInput(uploadStationsCommand);
        CsvReader.readLines(uploadStationsCommand.getStations().getInputStream(),
                line -> {
                    try {
                        stationInstallationResponse.updateResult(importStation(imageId, line));
                    } catch (Exception ex) {
                        log.error("Something went wrong during station import of line {}, and imageId {}", line, imageId, ex);
                        stationInstallationResponse.updateResult(LineImportResult.ERROR);
                    }
                });
        return stationInstallationResponse;
    }

    private LineImportResult importStation(Optional<ImageId> imageId, CsvReader.Line line) {
        Station stationFromCsv = stationFactory.createStation(line);
        imageId.ifPresent(image -> stationFromCsv.setImageId(image.getId()));
        final UUID stationId = stationFromCsv.getId();

        if (stationId == null) {
            return saveNewStation(stationFromCsv);
        }

        return stationsRepository.findById(stationId)
                .map(stationFromDatabase -> updateStation(stationFromCsv, stationFromDatabase))
                .orElse(LineImportResult.ERROR);
    }

    private LineImportResult saveNewStation(Station newStation) {
        stationsRepository.save(newStation);
        return LineImportResult.INSTALLED;
    }

    private LineImportResult updateStation(Station stationFromCsv, Station stationFromDatabase) {
        stationFromDatabase.apply(stationFromCsv);
        stationsRepository.save(stationFromDatabase);
        return LineImportResult.UPDATED;
    }

    private Optional<ImageId> getImageIdFromUsersInput(UploadStationsToCscCommand uploadStationsCommand) {
        return uploadStationsCommand.getImage()
                .map(imagesStorage::storeImage)
                .or(uploadStationsCommand::getImageId);
    }

}
