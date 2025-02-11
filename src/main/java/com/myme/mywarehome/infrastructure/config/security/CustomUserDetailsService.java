package com.myme.mywarehome.infrastructure.config.security;

import com.myme.mywarehome.domains.user.application.domain.User;
import com.myme.mywarehome.domains.user.application.domain.exception.UserNotFoundException;
import com.myme.mywarehome.domains.user.application.port.out.GetUserPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService, Serializable {
    private final GetUserPort getUserPort;

    @Override
    public UserDetails loadUserByUsername(String username) throws UserNotFoundException {

        User user = getUserPort.findUserById(username)
                .orElseThrow(UserNotFoundException::new);

        String roleName = user.getRole().getRoleName().replace("ROLE_", "");

        return new CustomUserDetails(
                user.getUserId(),  // PK
                user.getId(),      // 로그인 ID
                user.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + roleName))
        );
    }

}
