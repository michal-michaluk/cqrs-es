package devices.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.AnnotatedClassResolver;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.vladmihalcea.hibernate.type.util.ObjectMapperSupplier;
import devices.configuration.outbox.OutboxConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.PUBLIC_ONLY;

@Configuration
public class JsonConfiguration implements ObjectMapperSupplier {

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new ParameterNamesModule(JsonCreator.Mode.PROPERTIES))
            .registerModule(new Jdk8Module())
            .registerModule(new JavaTimeModule())
            .setVisibility(PropertyAccessor.CREATOR, ANY)
            .setVisibility(PropertyAccessor.FIELD, PUBLIC_ONLY)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .configure(MapperFeature.CAN_OVERRIDE_ACCESS_MODIFIERS, true)
            .configure(MapperFeature.AUTO_DETECT_FIELDS, true)
            .configure(SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS, false)
            .configure(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN, true)
            .addMixIn(DomainEvent.class, EventTypes.class);

    @Bean
    OutboxConfiguration outboxConfiguration() {
        return EventTypes.outbox;
    }

    @Bean
    ObjectMapper objectMapper() {
        return OBJECT_MAPPER;
    }

    @Bean
    MappingJackson2HttpMessageConverter jacksonMessageConverter() {
        return new MappingJackson2HttpMessageConverter(OBJECT_MAPPER);
    }

    /*
     * used for hibernate's object as json serialisation
     */
    @Override
    public ObjectMapper get() {
        return OBJECT_MAPPER;
    }

    @PostConstruct
    void initEventTypes() {
        DeserializationConfig config = OBJECT_MAPPER.getDeserializationConfig();
        AnnotationIntrospector ai = config.getAnnotationIntrospector();
        AnnotatedClass ac = AnnotatedClassResolver.resolve(config, OBJECT_MAPPER.constructType(DomainEvent.class), config);
        List<NamedType> subtypes = ai.findSubtypes(ac);

        EventTypes.init(subtypes.stream()
                .collect(Collectors.toMap(
                        NamedType::getType,
                        type -> EventTypes.Type.of(type.getName())
                ))
        );
    }
}
