package devices.configuration.features.stationManagement.installation;

import devices.configuration.features.catalogue.Ownership;
import lombok.Value;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Value
public class InstallStation {
    String cpo;

    String lcp;

    public boolean isSingleStepInstallation() {
        return isNotBlank(lcp);
    }

    public Ownership toOwnership() {
        return new Ownership(cpo, getLcp());
    }

    public String getLcp() {
        if (isNotBlank(lcp)) {
            return lcp;
        }

        return null;
    }
}
