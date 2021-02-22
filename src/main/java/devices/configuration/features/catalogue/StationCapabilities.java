package devices.configuration.features.catalogue;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Embeddable;

@Embeddable
@Data
@Accessors(chain = true)
public class StationCapabilities {
    private String availableOcppVersions;
    private Boolean debitPayment;
    private Boolean reservation;
    private Boolean creditPayment;
    private Boolean rfidReader;
    private Boolean remoteStart;
    private Boolean scnDlb;
    private Boolean tokenGrouping;
    private Boolean smartCharging;
    private Boolean unlock;
    private Boolean ac;
    private Boolean dc;

    String toStringCsv(String delimiter) {
        StringBuilder sb = new StringBuilder();
        sb.append(availableOcppVersions).append(delimiter);
        sb.append(booleanToString(debitPayment)).append(delimiter);
        sb.append(booleanToString(reservation)).append(delimiter);
        sb.append(booleanToString(creditPayment)).append(delimiter);
        sb.append(booleanToString(rfidReader)).append(delimiter);
        sb.append(booleanToString(remoteStart)).append(delimiter);
        sb.append(booleanToString(scnDlb)).append(delimiter);
        sb.append(booleanToString(tokenGrouping)).append(delimiter);
        sb.append(booleanToString(smartCharging)).append(delimiter);
        sb.append(booleanToString(unlock)).append(delimiter);
        sb.append(booleanToString(ac)).append(delimiter);
        sb.append(booleanToString(dc)).append(delimiter);

        return sb.toString();
    }

    private String booleanToString(Boolean value) {
        return value != null && value ? "YES" : "NO";
    }
}
