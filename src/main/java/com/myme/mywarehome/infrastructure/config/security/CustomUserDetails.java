package com.myme.mywarehome.infrastructure.config.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
public class CustomUserDetails extends org.springframework.security.core.userdetails.User {
    private final Long userId;  // PK
    private final String id;    // 로그인 ID

    public CustomUserDetails(Long userId, String id, String password, Collection<? extends GrantedAuthority> authorities) {
        super(id, password, authorities);
        this.userId = userId;
        this.id = id;
    }

}
