package devices.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.util.ReferenceSerializationConfigurer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.json.JacksonModuleRegistrar;
import springfox.documentation.spring.web.paths.RelativePathProvider;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.servlet.ServletContext;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration implements JacksonModuleRegistrar {

    private static boolean allowedPaths(String path) {
        return path != null && (
                path.startsWith("/installation") ||
                        path.startsWith("/stations/") ||
                        path.startsWith("/station/") ||
                        path.startsWith("/legacy/stations/") ||
                        path.startsWith("/toggles") ||
                        path.startsWith("/images") ||
                        path.startsWith("/registration/") ||
                        path.startsWith("/locations/") ||
                        path.startsWith("/stationDetails") ||
                        path.startsWith("/helloworld") ||
                        path.startsWith("/pub")
        );
    }

    @Override
    public void maybeRegisterModule(ObjectMapper objectMapper) {
        ReferenceSerializationConfigurer.serializeAsComputedRef(objectMapper);
    }

    @Bean
    public Docket swaggerDocumentation(@Value("${swagger.baseUrl:localhost}") String swaggerBaseUrl,
                                       @Value("${swagger.basePath:}") String swaggerBasePath,
                                       ServletContext servletContext) {

        return new Docket(DocumentationType.SWAGGER_2)
                .host(swaggerBaseUrl)
                .pathProvider(new RelativePathProvider(servletContext) {
                    @Override
                    protected String applicationPath() {
                        return "/" + swaggerBasePath;
                    }
                })
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(SwaggerConfiguration::allowedPaths)
                .build()
                .forCodeGeneration(true);
    }
}
