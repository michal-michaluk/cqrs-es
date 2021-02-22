package devices.configuration.blob;

import org.springframework.web.multipart.MultipartFile;

import java.net.URL;

public interface BlobStorage {
    URL uploadPhoto(MultipartFile multipartFile);

    void removePhoto(URL url);
}
