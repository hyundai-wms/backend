package com.myme.mywarehome.infrastructure.util.security;

import com.myme.mywarehome.domains.user.application.domain.Role;
import com.myme.mywarehome.domains.user.application.domain.exception.UnauthorizedException;
import com.myme.mywarehome.infrastructure.config.security.CustomUserDetails;
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

    /**
     * 유저의 id(PK) 가져오기
     * @return Long userId
     */
    public static Long getCurrentUserId() {
        CustomUserDetails userDetails = getCurrentUserDetails();
        return userDetails.getUserId();
    }

    /**
     * 유저의 로그인 ID 가져오기
     * @return String id
     */
    public static String getCurrentLoginId() {
        CustomUserDetails userDetails = getCurrentUserDetails();
        return userDetails.getId();
    }

    private static CustomUserDetails getCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
                authentication instanceof AnonymousAuthenticationToken) {
            throw new UnauthorizedException();
        }

        return (CustomUserDetails) authentication.getPrincipal();
    }

    /**
     * 현재 인증된 사용자의 Role을 가져 옴
     * @return List\<Role> roleList
     */
    public static List<Role> getCurrentUserRoles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
                authentication instanceof AnonymousAuthenticationToken) {
            throw new UnauthorizedException();
        }

        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(Role::valueOf)
                .collect(Collectors.toList());
    }

    /**
     * 현재 사용자가 특정 Role을 가지고 있는지 확인
     * @param role 체크하려는 Role
     * @return 현재 사용자가 해당 Role을 가지고 있다면 true를 반환
     */
    public static boolean hasRole(Role role) {
        return getCurrentUserRoles().contains(role);
    }
}
