package devices.configuration.remote;

import lombok.Value;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Value
public class IntervalRules {

    List<DeviceIdRule> deviceIdRules;
    List<ModelRule> modelRules;
    List<ProtocolRule> protocolRules;
    Duration def;

    static IntervalRules.DeviceIdRule deviceId(Duration duration, Set<String> deviceIds) {
        return new IntervalRules.DeviceIdRule(duration, deviceIds);
    }

    static IntervalRules.ModelRule model(Duration duration, String vendor, String model) {
        return new IntervalRules.ModelRule(duration, vendor, Pattern.compile(model));
    }

    static IntervalRules.ProtocolRule protocol(Duration duration, Protocol protocol) {
        return new IntervalRules.ProtocolRule(duration, protocol);
    }

    public Duration calculateInterval(Deviceish deviceish) {
        Optional<Duration> byDeviceId = deviceIdRules.stream()
                .filter(rule -> rule.test(deviceish))
                .findFirst()
                .map(DeviceIdRule::getInterval);
        Optional<Duration> byModel = modelRules.stream()
                .filter(rule -> rule.test(deviceish))
                .findFirst()
                .map(ModelRule::getInterval);
        Optional<Duration> byProtocol = protocolRules.stream()
                .filter(rule -> rule.test(deviceish))
                .findFirst()
                .map(ProtocolRule::getInterval);

        return Stream.of(byDeviceId, byModel, byProtocol)
                .filter(Optional::isPresent)
                .findFirst().map(Optional::get)
                .orElse(def);
    }

    @Value
    static class DeviceIdRule {
        Duration interval;
        Set<String> devices;

        boolean test(Deviceish device) {
            return devices.contains(device.getDeviceId());
        }
    }

    @Value
    static class ModelRule {
        Duration interval;
        String vendor;
        Pattern model;

        boolean test(Deviceish device) {
            return Objects.equals(device.getVendor(), vendor)
                    && model.matcher(device.getModel()).matches();
        }
    }

    @Value
    static class ProtocolRule {
        Duration interval;
        Protocol protocol;

        boolean test(Deviceish device) {
            return protocol == device.getProtocol();
        }
    }
}
