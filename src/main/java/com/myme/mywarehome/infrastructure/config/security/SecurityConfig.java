package com.myme.mywarehome.infrastructure.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myme.mywarehome.infrastructure.common.response.ErrorResponse;
import com.myme.mywarehome.infrastructure.exception.ErrorCode;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionFixationProtectionStrategy;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableRedisHttpSession
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityContext(securityContext -> securityContext
                        .securityContextRepository(new HttpSessionSecurityContextRepository())
                )
                .securityMatcher("/v1/**")
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cor -> cor.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers("/v1/auth/login", "/swagger-ui/**", "/v3/api-docs/**").permitAll()  // 로그인 경로만 허용
                                .requestMatchers("/v1/users/me").hasAnyRole("ADMIN", "MIDDLE_MANAGER", "WMS_MANAGER", "WORKER")
                                .requestMatchers("/v1/users/**").permitAll() // .hasRole("ADMIN")
                                .requestMatchers("/v1/**").hasAnyRole("ADMIN", "MIDDLE_MANAGER", "WMS_MANAGER", "WORKER")
                                .anyRequest().authenticated()
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .sessionFixation().changeSessionId()
                        .sessionAuthenticationStrategy(sessionAuthenticationStrategy())
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(false)
                        .sessionRegistry(sessionRegistry())
                        .expiredSessionStrategy(event -> {
                            HttpServletResponse response = event.getResponse();
                            response.setContentType("application/json;charset=UTF-8");
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

                            ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.SESSION_EXPIRED);
                            response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
                        })
                        .expiredUrl("/v1/auth/login")

                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {

                            log.debug("\n\n\n\n\n\n\n\n\n\n\n\n\n");
                            log.debug("디버깅:: " + authException.getMessage());
                            log.debug(exception.toString());

                            response.setContentType("application/json;charset=UTF-8");
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.UNAUTHORIZED);
                            response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
                        })
                )
                .logout(logout -> logout
                        .logoutUrl("/v1/auth/logout")
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setStatus(HttpServletResponse.SC_OK);
                        })
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true)
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "https://mywareho.me", "http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public ServletListenerRegistrationBean<HttpSessionEventPublisher> httpSessionEventPublisher() {
        return new ServletListenerRegistrationBean<>(new HttpSessionEventPublisher());
    }

    @Bean
    public SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        SessionFixationProtectionStrategy sessionFixationProtectionStrategy =
                new SessionFixationProtectionStrategy();
        sessionFixationProtectionStrategy.setMigrateSessionAttributes(true);
        return sessionFixationProtectionStrategy;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
