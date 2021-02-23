package devices.configuration.data;

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


    @Value
    @Builder
    public static class Visibility {
        AppVisibility mobileApp;
        boolean roamingEnabled;

        public enum AppVisibility {
            USABLE_AND_VISIBLE_ON_MAP,
            USABLE_BUT_HIDDEN_ON_MAP,
            INACCESSIBLE_AND_HIDDEN_ON_MAP
        }

        public static Visibility of(boolean usable, boolean showOnMap) {
            if (usable && showOnMap) return new Visibility(AppVisibility.USABLE_AND_VISIBLE_ON_MAP, true);
            if (usable) return new Visibility(AppVisibility.USABLE_BUT_HIDDEN_ON_MAP, false);
            return new Visibility(AppVisibility.INACCESSIBLE_AND_HIDDEN_ON_MAP, false);
        }
    }
}
