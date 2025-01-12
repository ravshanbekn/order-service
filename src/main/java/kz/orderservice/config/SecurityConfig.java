package kz.orderservice.config;

import kz.orderservice.entity.user.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(CsrfConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/register", "/login", "/v3/api-docs/**", "/swagger-ui/**",
                                "/swagger-ui.html", "/webjars/**").permitAll()
                        .requestMatchers(PUT, "/admin").hasAnyAuthority(Role.USER.name(), Role.ADMIN.name())
                        .requestMatchers(POST, "/orders").hasAnyAuthority(Role.USER.name(), Role.ADMIN.name())
                        .requestMatchers(PUT, "/orders/**").hasAnyAuthority(Role.USER.name(), Role.ADMIN.name())
                        .requestMatchers(GET, "/orders").hasAnyAuthority(Role.ADMIN.name())
                        .requestMatchers(GET, "/orders/**").hasAnyAuthority(Role.USER.name(), Role.ADMIN.name())
                        .requestMatchers(DELETE, "/orders/**").hasAnyAuthority(Role.ADMIN.name())
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .authenticationProvider(authenticationProvider)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }
}