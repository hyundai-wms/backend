package com.myme.mywarehome.infrastructure.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myme.mywarehome.infrastructure.common.response.ErrorResponse;
import com.myme.mywarehome.infrastructure.exception.ErrorCode;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyAuthoritiesMapper;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
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
                .securityMatcher("/v1/**")
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cor -> cor.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth ->
                        auth
                                // 공개 엔드포인트
                                .requestMatchers("/v1/auth/login", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                                .requestMatchers("/v1/productions/mrp/*/purchase/download",
                                        "/v1/productions/mrp/*/production/download",
                                        "/v1/productions/mrp/*/exception/download").permitAll()

                                .requestMatchers(
                                        "/v1/notifications/worker-test",
                                        "/v1/notifications/wms-manager-test",
                                        "v1/notifications/middle-manager-test",
                                        "v1/notifications/admin-test"
                                ).hasRole("ADMIN")

                                .requestMatchers("/v1/notifications/**").hasRole("WORKER")

                                // 자신의 정보 조회
                                .requestMatchers("/v1/users/me").hasRole("WORKER")

                                // 총 관리자 전용 엔드포인트
                                .requestMatchers(
                                        "/v1/storages/receipts/complete",
                                        "/v1/storages/issues/complete",
                                        "/v1/users/**"
                                ).hasRole("ADMIN")

                                // 중간 관리자 엔드포인트
                                .requestMatchers(
                                        "/v1/productions/**",
                                        "/v1/statistics/productions"
                                ).hasRole("MIDDLE_MANAGER")

                                // WMS 관리자 엔드포인트
                                .requestMatchers(
                                        "/v1/storages/inventories",
                                        "/v1/storages/inventories/stream",
                                        "/v1/storages/inventories/*/details",
                                        "/v1/statistics/storages"
                                ).hasRole("WMS_MANAGER")

                                // SSE 엔드포인트 및 나머지 모든 /v1/** 경로는 기본적으로 WORKER 이상의 권한 필요
                                .anyRequest().hasRole("WORKER")
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .maximumSessions(1)
                        .expiredSessionStrategy(event -> {
                            HttpServletResponse response = event.getResponse();
                            response.setContentType("application/json;charset=UTF-8");
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

                            ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.SESSION_EXPIRED);
                            response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
                        })

                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setContentType("application/json;charset=UTF-8");
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

                            log.debug("Authentication exception", authException);

                            ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.UNAUTHORIZED);
                            response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setContentType("application/json;charset=UTF-8");
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);

                            log.debug("Access Denied", accessDeniedException);

                            ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.FORBIDDEN);
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
    public RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.fromHierarchy("""
            ROLE_ADMIN > ROLE_MIDDLE_MANAGER
            ROLE_MIDDLE_MANAGER > ROLE_WMS_MANAGER
            ROLE_WMS_MANAGER > ROLE_WORKER
            """);
    }

    @Bean
    public DefaultWebSecurityExpressionHandler expressionHandler() {
        DefaultWebSecurityExpressionHandler expressionHandler = new DefaultWebSecurityExpressionHandler();
        expressionHandler.setRoleHierarchy(roleHierarchy());
        return expressionHandler;
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "https://mywareho.me", "http://localhost:5173", "https://dev.api.mywareho.me", "https://qa.api.mywareho.me", "http://localhost:8080"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));

        // SSE를 위한 헤더 설정
        configuration.addExposedHeader("Content-Type");
        // NGINX 프록시를 위한 설정
        configuration.addExposedHeader("X-Accel-Buffering");
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
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
