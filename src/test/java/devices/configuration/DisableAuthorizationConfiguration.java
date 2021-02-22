package devices.configuration;

import de.vattenfall.emobility.token.EmobilityAuthentication;
import de.vattenfall.emobility.token.JwtAuthenticationFailureHandler;
import de.vattenfall.emobility.token.JwtAuthenticationSuccessHandler;
import de.vattenfall.emobility.token.JwtTokens;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("!integration-test")
@Configuration
public class DisableAuthorizationConfiguration {

    public static final String DUMMY_TOKEN = "token";

    @Bean
    JwtTokens jwtTokens() {
        JwtTokens jwtTokens = Mockito.mock(JwtTokens.class);
        Mockito.when(jwtTokens.authenticate(Mockito.anyString())).thenReturn(new EmobilityAuthentication.Builder("kazik").build());
        return jwtTokens;
    }

    @Bean
    JwtAuthenticationSuccessHandler jwtAuthenticationSuccessHandler() {
        return new JwtAuthenticationSuccessHandler();
    }

    @Bean
    JwtAuthenticationFailureHandler jwtAuthenticationFailureHandler() {
        return new JwtAuthenticationFailureHandler();
    }
}
