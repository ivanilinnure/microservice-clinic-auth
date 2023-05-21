package com.clinic.auth.config;

import com.clinic.auth.exception.FilterChainExceptionHandler;
import com.clinic.auth.security.jwt.JwtConfigurer;
import com.clinic.auth.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.access.AccessDeniedHandler;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    public static final String ADMIN_ENDPOINT = "/api/v1/admin/**";
    public static final String AUTH_ENDPOINT = "/api/v1/auth/**";

    private final JwtTokenProvider jwtTokenProvider;
    private final FilterChainExceptionHandler filterChainExceptionHandler;
    private final AccessDeniedHandler accessDeniedHandler;

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // No session will be created or used by spring security
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.httpBasic().disable();

        // Disable CSRF (cross site request forgery)
        http.cors().and().csrf().disable();


        http.authorizeRequests()
                .antMatchers(AUTH_ENDPOINT).permitAll()
                .antMatchers(ADMIN_ENDPOINT).hasRole("ADMIN")
                .anyRequest().authenticated();

        // Apply JWT
        http.apply(new JwtConfigurer(jwtTokenProvider, filterChainExceptionHandler));

        http.exceptionHandling().accessDeniedHandler(accessDeniedHandler);

    }
}
