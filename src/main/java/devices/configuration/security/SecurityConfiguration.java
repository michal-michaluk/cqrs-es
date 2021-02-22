package devices.configuration.security;

import de.vattenfall.emobility.token.OboRolesMergingStrategy;
import de.vattenfall.emobility.token.OboRolesMergingStrategyProvider;
import de.vattenfall.emobility.token.authority.AuthorizedAs;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration  {

    @Bean
    AuthorizedAs authorizedAs() {
        return new AuthorizedAs();
    }

    @Bean
    OboRolesMergingStrategyProvider oboRolesMergingStrategyProvider() {
        return () -> OboRolesMergingStrategy.MERGE_OBO_WITH_MAIN_ROLES;
    }
}
