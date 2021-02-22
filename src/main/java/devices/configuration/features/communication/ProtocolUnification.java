package devices.configuration.features.communication;

import com.fasterxml.jackson.databind.ObjectMapper;

public interface ProtocolUnification {
    StationDetails toStationDetails(ObjectMapper mapper, String payload);
}
