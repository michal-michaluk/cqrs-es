package devices.configuration.blob;

import com.azure.core.http.rest.Response;
import com.azure.core.util.Context;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import devices.configuration.features.catalogue.StationException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.UUID;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.BooleanUtils.isFalse;
import static org.springframework.http.HttpStatus.*;

@Service
@Slf4j
@AllArgsConstructor
class BlobStorageService implements BlobStorage {

    private BlobContainerClient blobContainerClient;

    @Override
    public URL uploadPhoto(MultipartFile image) {
        String extension = FilenameUtils.getExtension(image.getOriginalFilename());
        String fileName = String.join(".", UUID.randomUUID().toString(), extension);
        BlobClient blobClient = blobContainerClient.getBlobClient(fileName);

        try {
            blobClient.upload(new BufferedInputStream(image.getInputStream()), image.getSize());
            return new URL(ofNullable(blobClient.getBlobUrl())
                    .orElseThrow(() -> StationException.blobConnectionError("Unable to retrieve uploaded file URL. Filename: " + fileName)));
        } catch (IOException e) {
            throw StationException.blobConnectionError(format("IOException occurred. Filename: %s. Message: %s", fileName, e.getMessage()));
        }
    }

    @Override
    public void removePhoto(URL url) {
        String filename = url.toString().substring(blobContainerClient.getBlobContainerUrl().length() + 1);
        BlobClient blobClient = blobContainerClient.getBlobClient(filename);

        if (isFalse(blobClient.exists())) {
            log.error("Blob not found during removal: " + url);
            return;
        }

        Response<Void> response = blobClient.deleteWithResponse(null, null, null, Context.NONE);

        HttpStatus responseStatus = valueOf(response.getStatusCode());
        if (!responseStatus.is2xxSuccessful()) {
            throw StationException.blobConnectionError(format("Unable to delete blob %s Response code: %d", url.toString(), response.getStatusCode()));
        }
    }


}