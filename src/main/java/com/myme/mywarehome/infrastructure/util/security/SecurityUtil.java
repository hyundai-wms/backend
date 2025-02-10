package com.myme.mywarehome.infrastructure.util.security;

import com.myme.mywarehome.domains.user.application.domain.exception.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class SecurityUtil {
    private SecurityUtil() {
        throw new IllegalStateException("Utility class");
    }

    // 현재 인증된 사용자의 ID를 가져옴
    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
                authentication instanceof AnonymousAuthenticationToken) {
            throw new UnauthorizedException();
        }

        return Long.parseLong(authentication.getName());
    }

    // 현재 인증된 사용자의 Role을 가져옴
    public static List<String> getCurrentUserRoles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
                authentication instanceof AnonymousAuthenticationToken) {
            throw new UnauthorizedException();
        }

        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
    }

    // 현재 사용자가 특정 role을 가지고 있는지 확인
    public static boolean hasRole(String role) {
        return getCurrentUserRoles().contains("ROLE_" + role);
    }
}
