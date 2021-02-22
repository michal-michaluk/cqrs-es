package devices.configuration.features.images;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
class ImagesListResponse {

    List<String> imagesIds = new ArrayList<>();
}
