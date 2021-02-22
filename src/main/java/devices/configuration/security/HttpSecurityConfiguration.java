package devices.configuration.security;

import de.vattenfall.emobility.token.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;

@Configuration
@RequiredArgsConstructor
public class HttpSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final JwtTokens jwtTokens;
    private final JwtAuthenticationSuccessHandler jwtAuthenticationSuccessHandler;
    private final JwtAuthenticationFailureHandler jwtAuthenticationFailureHandler;

    private final String[] ignoredPaths = new String[]{
            "/actuator/**",
            "/v2/api-docs",
            "/stations/**",
            "/protocols/**",
            "/ocpp20/**",
            "/ocpp/**",
            "/int/**",
            "/prv/**"
    };

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .cors().and()
                .httpBasic().disable()
                .authorizeRequests()
                .antMatchers(ignoredPaths).permitAll()
                .and()
                .headers().contentSecurityPolicy("connect-src 'self' https://*.azure-api.net").and()
                .and()
                .addFilterAfter(pathIgnoringJwtFilter(), BasicAuthenticationFilter.class)
        ;
    }

    private PathIgnoringFilter pathIgnoringJwtFilter() throws Exception {
        PathIgnoringFilter pathIgnoringFilter = new PathIgnoringFilter(jwtAuthenticationProcessingFilter());
        pathIgnoringFilter.setIgnoredUrlPaths(ignoredPaths);
        return pathIgnoringFilter;
    }

    private JwtAuthenticationProcessingFilter jwtAuthenticationProcessingFilter() throws Exception {
        JwtAuthenticationProcessingFilter jwtAuthenticationProcessingFilter = new JwtAuthenticationProcessingFilter(jwtTokens, AnyRequestMatcher.INSTANCE);
        jwtAuthenticationProcessingFilter.setAuthenticationManager(authenticationManager());
        jwtAuthenticationProcessingFilter.setAuthenticationSuccessHandler(jwtAuthenticationSuccessHandler);
        jwtAuthenticationProcessingFilter.setAuthenticationFailureHandler(jwtAuthenticationFailureHandler);
        return jwtAuthenticationProcessingFilter;
    }
}
