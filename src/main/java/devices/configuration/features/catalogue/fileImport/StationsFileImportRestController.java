package devices.configuration.features.catalogue.fileImport;

import devices.configuration.features.FileSizeValidator;
import devices.configuration.features.catalogue.exceptions.ErrorMessage;
import devices.configuration.features.catalogue.fileImport.csc.StationInstallationToCscResponse;
import devices.configuration.features.catalogue.fileImport.csc.StationsFileImportToCscService;
import devices.configuration.features.catalogue.fileImport.csc.UploadStationsToCscCommand;
import devices.configuration.features.catalogue.fileImport.eggplant.Country;
import devices.configuration.features.catalogue.fileImport.eggplant.StationsFileImportReport;
import devices.configuration.features.eggplant.EmobilityOldPlatformClient;
import devices.configuration.features.eggplant.ImportRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.status;

@RestController
@RequiredArgsConstructor
@Slf4j
class StationsFileImportRestController {

    public static final String LEGACY_IMPORT_HEADER = "Legacy-Import";

    private final StationsFileImportToCscService fileImportToCscService;
    private final EmobilityOldPlatformClient oldPlatformClient;

    @Value("${features.images.sizeLimitInMb:2}")
    private int imageSizeLimit;

    @PostMapping(value = "/installation/legacy/stations")
    public ResponseEntity<StationsFileImportReport> handleFileUploadToEggplant(
            @RequestParam(value = "stations") MultipartFile stations,
            @RequestParam(value = "country") Country country,
            @RequestParam(value = "chargingStationTypeName") String chargingStationTypeName,
            @RequestParam(value = "chargingPointTypeName") String chargingPointTypeName,
            @RequestParam(value = "cpoName") String cpoName,
            @RequestParam(value = "requestor") String username) {

        ImportRequest request = new ImportRequest(country, chargingStationTypeName, chargingPointTypeName, cpoName);
        StationsFileImportReport report = oldPlatformClient.importStationsFile(stations, request, username);
        return status(CREATED).body(report);
    }

    @PostMapping("/installation/stations")
    public ResponseEntity<?> handleFileUpload(
            @RequestParam(value = "stations") MultipartFile stations,
            @RequestParam(value = "stationsPhoto", required = false) MultipartFile stationsPhoto,
            @RequestParam(value = "stationsPhotoId", required = false) String stationsPhotoId
    ) throws IOException {
        var uploadStationsCommandBuilder = new UploadStationsToCscCommand.Builder(stations);

        if (stationsPhoto != null) {
            var sizeError = validate(stationsPhoto);
            if (sizeError.isPresent()) {
                return badRequest().body(sizeError.get());
            }

            uploadStationsCommandBuilder = uploadStationsCommandBuilder.withImage(stationsPhoto);
        }

        if (stationsPhotoId != null) {
            uploadStationsCommandBuilder = uploadStationsCommandBuilder.withImageReference(stationsPhotoId);
        }

        StationInstallationToCscResponse response = fileImportToCscService.uploadStations(uploadStationsCommandBuilder.build());
        return status(CREATED).body(response);
    }

    @GetMapping(
            value = "/installation/stations-template",
            produces = "text/csv",
            headers = LEGACY_IMPORT_HEADER)
    @ResponseBody
    public ClassPathResource getStationsLegacyFileExample() {
        return new ClassPathResource("TEMPLATE_Stations_import_file.csv");
    }

    @GetMapping(
            value = "/installation/stations-template",
            produces = "text/csv")
    @ResponseBody
    public ClassPathResource getStationsFileExample() {
        return new ClassPathResource("stations_import_example.csv");
    }

    private Optional<ErrorMessage> validate(MultipartFile stationsPhoto) {
        return new FileSizeValidator(imageSizeLimit).validate(stationsPhoto);
    }
}
