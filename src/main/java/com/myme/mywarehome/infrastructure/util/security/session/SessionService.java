package com.myme.mywarehome.infrastructure.util.security.session;

import com.myme.mywarehome.domains.user.application.domain.Role;
import com.myme.mywarehome.infrastructure.config.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SessionService {
    private final RedisTemplate<String, Object> redisTemplate;

    public void updateUserSessionRole(Long userId, Role newRole) {
        // 1. 모든 세션 키 찾기
        Set<String> sessionKeys = redisTemplate.keys("spring:session:sessions:*");

        for (String sessionKey : sessionKeys) {
            // 2. 각 세션의 SecurityContext를 가져옴
            Object securityContextObj = redisTemplate.opsForHash().get(
                    sessionKey,
                    "sessionAttr:SPRING_SECURITY_CONTEXT"
            );

            if (securityContextObj instanceof SecurityContext securityContext) {
                Authentication auth = securityContext.getAuthentication();

                // 3. 현재 세션이 타겟 유저의 세션인지 확인
                if (auth != null && auth.getPrincipal() instanceof CustomUserDetails userDetails) {
                    if (userDetails.getUserId().equals(userId)) {
                        // 4. Role 업데이트
                        List<GrantedAuthority> updatedAuthorities = List.of(
                                new SimpleGrantedAuthority(newRole.name())
                        );

                        Authentication newAuth = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                auth.getCredentials(),
                                updatedAuthorities
                        );

                        securityContext.setAuthentication(newAuth);

                        // 5. 업데이트된 SecurityContext 저장
                        redisTemplate.opsForHash().put(
                                sessionKey,
                                "sessionAttr:SPRING_SECURITY_CONTEXT",
                                securityContext
                        );
                    }
                }
            }
        }
    }
}
