package devices.configuration;

import io.swagger.parser.SwaggerParser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration-test")
class SwaggerConfigurationTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    void Swagger_should_contains_installation_in_path() {
        // when
        String swaggerDocument = getDocumentation();

        // then
        assertThat(swaggerDocument).contains("/installation");
    }

    @Test
    void Valid_documentation_should_be_available() throws IOException {
        // when
        String swaggerDocumentation = getDocumentation();

        // then
        isNotBlank(swaggerDocumentation);
        saveAsArtifact(swaggerDocumentation);
    }

    @Test
    void Documentation_should_be_parsable() {
        // when
        String swaggerDocumentation = getDocumentation();
        SwaggerParser swaggerParser = new SwaggerParser();

        // expect
        assertDoesNotThrow(() -> swaggerParser.parse(swaggerDocumentation));
    }

    private String getDocumentation() {
        return testRestTemplate.getForEntity("/v2/api-docs", String.class).getBody();
    }

    private void saveAsArtifact(String documentation) throws IOException {
        String targetPath = "./build/api-docs/";
        String fileName = "swagger.json";
        BufferedWriter writer = null;
        try {
            Files.createDirectories(Paths.get(targetPath));
            writer = Files.newBufferedWriter(Paths.get(targetPath, fileName), StandardCharsets.UTF_8);
            writer.write(documentation);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }
}
