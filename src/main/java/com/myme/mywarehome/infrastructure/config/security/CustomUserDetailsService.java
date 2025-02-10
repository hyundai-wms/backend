package com.myme.mywarehome.infrastructure.config.security;

import com.myme.mywarehome.domains.user.application.domain.User;
import com.myme.mywarehome.domains.user.application.domain.exception.UserNotFoundException;
import com.myme.mywarehome.domains.user.application.port.out.GetUserPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final GetUserPort getUserPort;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("[디버깅] CustomUserDetailsService.loadUserByUsername called with username: {}", username);  // info 레벨로 변경

        User user = getUserPort.findUserById(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + username));

        log.debug("[디버깅] User found: {}, Role: {}", user.getId(), user.getRole());  // info 레벨로 변경

        String roleName = user.getRole().getRoleName().replace("ROLE_", "");
        log.debug("[디버깅] Processed role name: {}", roleName);  // info 레벨로 변경

        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(user.getId())
                .password(user.getPassword())
                .roles(roleName)
                .build();

        log.debug("[디버깅] Created UserDetails with authorities: {}", userDetails.getAuthorities());  // info 레벨로 변경

        return userDetails;
    }
}
