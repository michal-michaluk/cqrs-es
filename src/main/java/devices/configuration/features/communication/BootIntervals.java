package devices.configuration.features.communication;

import org.springframework.stereotype.Service;

@Service
public class BootIntervals {
    public int heartbeatInterval(Endpoint endpoint) {
        return 7200;
    }
}
