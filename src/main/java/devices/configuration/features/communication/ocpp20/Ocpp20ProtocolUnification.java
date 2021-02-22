package devices.configuration.features.communication.ocpp20;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import devices.configuration.features.communication.ProtocolUnification;
import devices.configuration.features.communication.StationDetails;

public class Ocpp20ProtocolUnification implements ProtocolUnification {

    @Override
    public StationDetails toStationDetails(ObjectMapper mapper, String payload) {
        try {
            return mapper.readValue(payload, BootNotificationRequest.class).toStationDetails();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error occurred while parsing payload to Station Details:" + e.getLocalizedMessage());
        }
    }
}
