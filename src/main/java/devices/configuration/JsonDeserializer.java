package devices.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class JsonDeserializer {

    private static final Logger log = LoggerFactory.getLogger(JsonDeserializer.class);

    private final ObjectMapper objectMapper;

    public <T> T readValue(String content, Class<T> valueType, T defaultValue) {
        try {
            return objectMapper.readValue(content, valueType);
        } catch (JsonProcessingException exception) {
            log.warn("Failed to deserialize json: {}", content, exception);
            return defaultValue;
        }
    }
}
