package devices.configuration.features.catalogue.fileImport.eggplant;

import devices.configuration.features.eggplant.ImportRequest;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

@Builder
class UploadStationsToEggplantCommand {
    MultipartFile stations;
    ImportRequest request;
}
