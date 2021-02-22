package devices.configuration.legacy;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

@Value
class EventType {

    String type;

    @JsonCreator
    EventType(@JsonProperty("type") String type) {
        this.type = type;
    }
}
