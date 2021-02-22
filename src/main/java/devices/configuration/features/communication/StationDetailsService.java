package devices.configuration.features.communication;

import devices.configuration.features.communication.persistence.MessageRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@AllArgsConstructor
@Service
public class StationDetailsService {

    private final MessageRepository messageRepository;

    public Optional<StationDetails> getStationDetailByStationName(String stationName) {
        return messageRepository.getStationDetails(stationName);
    }
}
