package devices.configuration.features.catalogue;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Ownership {
    private String cpo;
    private String lcp;

    public static Ownership of(String cpo, String lcp) {
        Ownership ownership = new Ownership();
        ownership.setCpo(cpo);
        ownership.setLcp(lcp);
        return ownership;
    }

    public static Ownership of(String cpo) {
        Ownership ownership = new Ownership();
        ownership.setCpo(cpo);
        return ownership;
    }

    public Ownership withLcp(String lcp) {
        return new Ownership(cpo, lcp);
    }
}
