package devices.configuration;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

public class FileFixture {



    public static MockMultipartFile mockImageWithSize(int megabytes) {
        return mockImageWithSize(megabytes, "stationsPhoto");
    }

    public static MockMultipartFile mockImageWithSize(int megabytes, String name) {
        return new MockMultipartFile(name, "image.jpg", MediaType.IMAGE_JPEG_VALUE, new byte[megabytes * 1024 * 1024]);
    }

    public static MockMultipartFile mockCsvFile() {
        return new MockMultipartFile("stations", "stations.csv", null, "column A;column B\nvalue A;value B\n another value A;another value B".getBytes());
    }


    public static MockMultipartFile mockCsvFile(String content) {
        return new MockMultipartFile("stations", "stations.csv", null, content.getBytes());
    }

    public static FileSystemResource imageFile() {
        return new FileSystemResource("./src/test/resources/station.jpeg");
    }


}
