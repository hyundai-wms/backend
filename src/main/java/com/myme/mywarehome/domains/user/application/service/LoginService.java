package com.myme.mywarehome.domains.user.application.service;

import com.myme.mywarehome.domains.user.application.domain.User;
import com.myme.mywarehome.domains.user.application.domain.exception.InvalidCredentialsException;
import com.myme.mywarehome.domains.user.application.domain.exception.LoginFailedException;
import com.myme.mywarehome.domains.user.application.domain.exception.UserNotFoundException;
import com.myme.mywarehome.domains.user.application.port.in.LoginUseCase;
import com.myme.mywarehome.domains.user.application.port.out.GetUserPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService implements LoginUseCase {
    private final AuthenticationManager authenticationManager;
    private final GetUserPort getUserPort;

    @Override
    public User login(String id, String password) {
        try {
            log.info("Attempting login for user: {}", id);

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(id, password)
            );

            log.info("Authentication successful. Authorities: {}",
                    authentication.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);

            Authentication storedAuth = SecurityContextHolder.getContext().getAuthentication();
            log.info("Stored Authentication in SecurityContext: {}", storedAuth);
            log.info("Stored Authorities: {}", storedAuth.getAuthorities());

            return getUserPort.findUserById(id)
                    .orElseThrow(UserNotFoundException::new);
        } catch (BadCredentialsException e) {
            log.error("Login failed - Bad credentials for user: {}", id);
            throw new LoginFailedException();
        } catch (AuthenticationException e) {
            log.error("Login failed - Authentication exception: {}", e.getMessage());
            throw new InvalidCredentialsException();
        }
    }
}
