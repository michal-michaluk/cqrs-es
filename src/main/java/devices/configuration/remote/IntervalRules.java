package devices.configuration.remote;

import lombok.Value;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;

@Value
public class IntervalRules {

    List<DeviceIdRule> byIds;
    List<ModelRule> byModel;
    List<ProtocolRule> byProtocol;
    Duration def;

    public static DeviceIdRule byDeviceIdRule(Duration interval, Set<String> deviceId) {
        return new DeviceIdRule(interval, deviceId);
    }

    public static ModelRule byModelRule(Duration interval, String vendor, Pattern model) {
        return new ModelRule(interval, vendor, model);
    }

    public static ProtocolRule byProtocolRule(Duration interval, Protocols protocol) {
        return new ProtocolRule(interval, protocol);
    }

    public static IntervalRules defaultRules() {
        return new IntervalRules(List.of(), List.of(), List.of(), Duration.ofSeconds(2700));
    }

    public Duration calculateInterval(Deviceish device) {
        Optional<Duration> byDeviceIdMatch = byIds.stream()
                .filter(rule -> rule.test(device))
                .findFirst()
                .map(DeviceIdRule::getInterval);

        if (byDeviceIdMatch.isPresent()) {
            return byDeviceIdMatch.get();
        }
        Optional<Duration> byModelIdMatch = byModel.stream()
                .filter(rule -> rule.test(device))
                .findFirst()
                .map(ModelRule::getInterval);

        if (byModelIdMatch.isPresent()) {
            return byModelIdMatch.get();
        }
        Optional<Duration> byProtocolIdMatch = byProtocol.stream()
                .filter(rule -> rule.test(device))
                .findFirst()
                .map(ProtocolRule::getInterval);

        return byProtocolIdMatch.orElse(def);
    }

    @Value
    static class DeviceIdRule implements Predicate<Deviceish> {

        Duration interval;
        Set<String> devices;

        @Override
        public boolean test(Deviceish device) {
            return devices.contains(device.getDeviceId());
        }
    }

    @Value
    static class ModelRule implements Predicate<Deviceish> {
        Duration interval;
        String vendor;
        Pattern model;

        @Override
        public boolean test(Deviceish device) {
            return Objects.equals(vendor, device.getVendor())
                    && model.matcher(device.getModel()).matches();
        }
    }

    @Value
    static class ProtocolRule implements Predicate<Deviceish> {
        Duration interval;
        Protocols protocol;

        @Override
        public boolean test(Deviceish device) {
            return Objects.equals(protocol, device.getProtocol());
        }
    }
}
