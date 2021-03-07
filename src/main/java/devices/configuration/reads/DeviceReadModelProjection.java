package devices.configuration.reads;

import devices.configuration.device.DeviceSnapshot;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class DeviceReadModelProjection {

    private final DeviceReadModelRepository repository;

    @Transactional
    @TransactionalEventListener
    public void onDeviceChanged(DeviceSnapshot device) {
        repository.findById(device.getDeviceId())
                .orElseGet(() -> repository.save(new DeviceReadModelEntity(device.getDeviceId())))
                .setSnapshot(device);
    }

    @KafkaListener(topics = "crm-account-snapshot-v1")
    public void onCustomerChanged(@Payload CrmAccountUpdate account) {
        repository.findByOperator(account.getPartyKey())
                .forEach(entity -> entity.setOperatorDetails(account.getDetails()));
        repository.findByProvider(account.getPartyKey())
                .forEach(entity -> entity.setProviderDetails(account.getDetails()));
    }
}
