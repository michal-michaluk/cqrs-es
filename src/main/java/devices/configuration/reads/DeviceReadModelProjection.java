package devices.configuration.reads;

import devices.configuration.device.DeviceSnapshot;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@AllArgsConstructor
public class DeviceReadModelProjection {

    private final DeviceReadModelRepository repository;

    @Transactional
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onDeviceChanged(DeviceSnapshot device) {
        repository.findById(device.getDeviceId())
                .orElseGet(() -> repository.save(new DeviceReadModelEntity(device.getDeviceId())))
                .setSnapshot(device);
    }

    @Transactional
    @KafkaListener(topics = "crm-account-snapshot-v1")
    public void onCustomerChanged(@Payload CrmAccountUpdate account) {
        repository.findByOperator(account.getPartyKey())
                .forEach(entity -> entity.setOperatorDetails(account.getDetails()));
        repository.findByProvider(account.getPartyKey())
                .forEach(entity -> entity.setProviderDetails(account.getDetails()));
    }
}
