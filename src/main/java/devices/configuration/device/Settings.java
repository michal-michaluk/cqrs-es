package devices.configuration.device;

import lombok.Builder;
import lombok.Value;

import static java.util.Optional.ofNullable;

@Value
@Builder(toBuilder = true)
public class Settings {
    Boolean autoStart;
    Boolean remoteControl;

    Boolean billing;
    Boolean reimbursement;

    Boolean showOnMap;
    Boolean publicAccess;

    public static Settings defaultSettings() {
        return builder()
                .autoStart(false)
                .remoteControl(false)
                .billing(false)
                .reimbursement(false)
                .showOnMap(false)
                .publicAccess(false)
                .build();
    }

    public boolean isAutoStart() {
        return autoStart != null && autoStart;
    }

    public boolean isRemoteControl() {
        return remoteControl != null && remoteControl;
    }

    public boolean isBilling() {
        return billing != null && billing;
    }

    public boolean isReimbursement() {
        return reimbursement != null && reimbursement;
    }

    public boolean isShowOnMap() {
        return showOnMap != null && showOnMap;
    }

    public boolean isPublicAccess() {
        return publicAccess != null && publicAccess;
    }

    public Settings merge(Settings other) {
        SettingsBuilder merged = this.toBuilder();
        ofNullable(other.autoStart).ifPresent(merged::autoStart);
        ofNullable(other.remoteControl).ifPresent(merged::remoteControl);
        ofNullable(other.billing).ifPresent(merged::billing);
        ofNullable(other.reimbursement).ifPresent(merged::reimbursement);
        ofNullable(other.showOnMap).ifPresent(merged::showOnMap);
        ofNullable(other.publicAccess).ifPresent(merged::publicAccess);
        return merged.build();
    }
}