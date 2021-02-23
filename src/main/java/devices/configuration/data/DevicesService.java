package devices.configuration.data;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DevicesService {

    private final DeviceRepository repository;

    @Transactional
    public Optional<Device> updateDevice(String device, UpdateDevice command) {
        return repository
                .findByName(device)
                .map(station -> {
                    command.onOwnershipUpdate(station::assigne)
                            .onLocationUpdate(station::updateLocation)
                            .onOpeningUpdate(station::updateOpening)
                            .onSettingsUpdate(station::updateSettings);
                    return repository.save(station);
                });
    }
}
