package devices.configuration.device;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.awt.print.Pageable;
import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequiredArgsConstructor
public class DevicesController {

    private final DeviceRepository repository;
    private final DeviceService service;

    @Transactional(readOnly = true)
    @GetMapping(path = "/devices", params = {"page", "size"},
            produces = APPLICATION_JSON_VALUE)
    public Page<DeviceSnapshot> get(Pageable pageable) {
        return repository.findAll(pageable)
                .map(Device::toSnapshot);
    }

    @Transactional(readOnly = true)
    @GetMapping(path = "/devices/{deviceId}",
            produces = APPLICATION_JSON_VALUE)
    public Optional<DeviceSnapshot> get(@PathVariable String deviceId) {
        return repository.findByDeviceId(deviceId)
                .map(Device::toSnapshot);
    }

    @Transactional
    @PatchMapping(path = "/devices/{deviceId}",
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public DeviceSnapshot patchStation(@PathVariable String deviceId,
                                       @RequestBody @Valid UpdateDevice update) {
        return service.update(deviceId, update)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}
