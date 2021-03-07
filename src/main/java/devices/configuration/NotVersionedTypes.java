package devices.configuration;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.AnnotatedClassResolver;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
public class NotVersionedTypes<T> {

    private final Map<? extends Class<?>, String> types;

    public static <T> NotVersionedTypes<T> classToNotVersionedType(ObjectMapper mapper, Class<T> baseInterface) {
        DeserializationConfig config = mapper.getDeserializationConfig();
        AnnotationIntrospector ai = config.getAnnotationIntrospector();
        AnnotatedClass ac = AnnotatedClassResolver.resolve(config, JsonConfiguration.OBJECT_MAPPER.constructType(baseInterface), config);
        List<NamedType> subtypes = ai.findSubtypes(ac);

        return new NotVersionedTypes<>(subtypes.stream()
                .collect(Collectors.toMap(
                        NamedType::getType,
                        type -> of(type.getName(), baseInterface)
                )));
    }

    private static String of(String typeName, Class<?> baseInterface) {
        String[] parts = typeName.split("_v");
        if (parts.length != 2 || StringUtils.isBlank(parts[1])) {
            throw new IllegalArgumentException(
                    "Version required in " + baseInterface.getName() + " JsonSubTypes name, like DeviceProtocolChanged_v1, '_v' part is important, thrown for type name: " + typeName
            );
        }
        return parts[0];
    }

    public String get(Class<? extends T> type) {
        return types.get(type);
    }
}
