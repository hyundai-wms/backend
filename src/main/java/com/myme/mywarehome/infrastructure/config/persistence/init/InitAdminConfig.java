package com.myme.mywarehome.infrastructure.config.persistence.init;

import com.myme.mywarehome.domains.user.adapter.out.persistence.UserJpaRepository;
import com.myme.mywarehome.domains.user.application.domain.User;
import com.myme.mywarehome.domains.user.application.domain.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InitAdminConfig implements CommandLineRunner {
    private final PasswordEncoder passwordEncoder;
    private final UserJpaRepository userJpaRepository;

    @Override
    public void run(String... args) {
        if (!userJpaRepository.existsById("admin")) {
            User admin = User.builder()
                    .name("총관리자")
                    .phoneNumber("01012345678")
                    .id("admin")
                    .password(passwordEncoder.encode("1234"))
                    .role(Role.ROLE_ADMIN)
                    .build();

            userJpaRepository.save(admin);
        }
    }
}
