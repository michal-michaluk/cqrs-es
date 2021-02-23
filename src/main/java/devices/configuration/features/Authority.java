package devices.configuration.features;

import devices.configuration.data.Ownership;
import devices.configuration.data.StationException;
import de.vattenfall.emobility.token.EmobilityAuthentication;
import de.vattenfall.emobility.token.authority.ChargingPointOperatorAuthority;
import de.vattenfall.emobility.token.authority.LocalChargeProviderAuthority;
import lombok.Value;

import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

@Value
public class Authority {

    Set<String> cpo;
    String lcp;

    public static Authority of(EmobilityAuthentication authentication) {
        if (authentication == null) {
            return new Authority(Set.of(), null);
        }
        Optional<LocalChargeProviderAuthority> lcp = authentication.getAuthorityIfAssigned(LocalChargeProviderAuthority.class);
        Optional<ChargingPointOperatorAuthority> cpo = authentication.getAuthorityIfAssigned(ChargingPointOperatorAuthority.class);

        return new Authority(
                cpo.map(ChargingPointOperatorAuthority::getCpoNames)
                        .map(list -> Set.of(list.toArray(new String[0])))
                        .orElse(Set.of()),
                lcp.map(LocalChargeProviderAuthority::getLcpName)
                        .orElse(null)
        );
    }

    public static Set<String> requireCpo(EmobilityAuthentication auth) {
        return Authority.of(auth).getCpo(StationException::requiredCpoAuthority);
    }

    public static String requireLcp(EmobilityAuthentication auth) {
        return Authority.of(auth).getLcp(StationException::requiredLcpAuthority);
    }

    public Set<String> getCpo() {
        return cpo;
    }

    public Optional<String> getLcp() {
        return Optional.ofNullable(lcp);
    }

    public <X extends Throwable> String getLcp(Supplier<? extends X> exceptionSupplier) throws X {
        if (lcp == null) {
            throw exceptionSupplier.get();
        }
        return lcp;
    }

    public <X extends Throwable> Set<String> getCpo(Supplier<? extends X> exceptionSupplier) throws X {
        if (cpo.isEmpty()) {
            throw exceptionSupplier.get();
        }
        return cpo;
    }

    public boolean matches(Ownership ownership) {
        if (cpo.contains(ownership.getCpo())) {
            return true;
        }
        return lcp != null && lcp.equals(ownership.getLcp());
    }
}
