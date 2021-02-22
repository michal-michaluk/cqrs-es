package devices.configuration;

import de.vattenfall.emobility.token.EmobilityAuthentication;
import de.vattenfall.emobility.token.JwtTokens;
import de.vattenfall.emobility.token.authority.ChargingPointOperatorAuthority;
import org.springframework.stereotype.Component;

import static java.util.Collections.singletonList;

@Component
public class SecurityFixture {

    private static final String operator = "testOperator";
    public static final String DEFAULT_CPO = "InCharge SE";

    private final JwtTokens jwtTokens;

    SecurityFixture(JwtTokens jwtTokens) {
        this.jwtTokens = jwtTokens;
    }

    public String cpoValidToken(String cpo, String userDisplayName) {

        return jwtTokens.generateToken(cpoAuth(userDisplayName, cpo));
    }

    public String operatorToken() {
        return operatorToken(operator);
    }

    public String operatorToken(String name) {
        return jwtTokens.generateToken(cpoAuth(name, DEFAULT_CPO));
    }

    public static EmobilityAuthentication cpoAuth(String userDisplayName, String cpo) {
        return new EmobilityAuthentication.Builder(cpo)
                .withAuthority(new ChargingPointOperatorAuthority(singletonList(cpo)))
                .withDisplayName(userDisplayName).build();
    }
}
