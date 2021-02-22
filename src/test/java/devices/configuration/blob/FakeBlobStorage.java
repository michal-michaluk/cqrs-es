package devices.configuration.blob;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

public class FakeBlobStorage implements BlobStorage {

    private List<URL> storage = new ArrayList<>();
    private static final String BASE_URL = "http://mockedBlobStorageAddress/media/";

    @Override
    public URL uploadPhoto(MultipartFile picture) {
        URL url;
        try {
            url = new URL(BASE_URL + picture.getName());
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Test URL was wrong!");
        }

        storage.add(url);

        return url;
    }

    @Override
    public void removePhoto(URL url) {
        if (!storage.contains(url)) {
            throw new ResponseStatusException(NOT_FOUND, "Blob not found: " + url);
        }

        storage.remove(url);
    }
}
