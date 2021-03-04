package devices.configuration.device;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DeviceService {

    private final DeviceRepository repository;

    @Transactional
    public Optional<DeviceSnapshot> update(String deviceId, UpdateDevice update) {
        return repository.findByDeviceId(deviceId)
                .map(device -> {
                    update
                            .onLocationUpdate(device::updateLocation)
                            .onOpeningUpdate(device::updateOpeningHours)
                            .onSettingsUpdate(device::updateSettings)
                            .onOwnershipUpdate(device::assignTo);
                    repository.save(device);
                    return device.toSnapshot();
                });
    }
}