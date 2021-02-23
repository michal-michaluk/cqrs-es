package devices.configuration.data;

import lombok.Getter;

@Getter
public enum ConnectorType {
    CHADEMO("CHAdeMO"),
    TYPE_1_SAE_J1772("IEC 62196 Type 1 \"SAE J1772\""),
    TYPE_2_MENNEKES("IEC 62196 Type 2 \"Mennekes\""),
    TYPE_2_COMBO("IEC 62196 Type 2 Combo");

    private final String fullName;

    ConnectorType(String fullName) {
        this.fullName = fullName;
    }
}
