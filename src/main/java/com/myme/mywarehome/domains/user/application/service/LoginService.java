package com.myme.mywarehome.domains.user.application.service;

import com.myme.mywarehome.domains.user.application.domain.User;
import com.myme.mywarehome.domains.user.application.exception.InvalidCredentialsException;
import com.myme.mywarehome.domains.user.application.exception.LoginFailedException;
import com.myme.mywarehome.domains.user.application.exception.UserNotFoundException;
import com.myme.mywarehome.domains.user.application.port.in.LoginUseCase;
import com.myme.mywarehome.domains.user.application.port.out.GetUserPort;
import com.myme.mywarehome.infrastructure.aspect.lock.UserLock;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService implements LoginUseCase {
    private final AuthenticationManager authenticationManager;
    private final GetUserPort getUserPort;
    private final HttpSession httpSession;

    @UserLock
    @Override
    public User login(String id, String password) {
        try {
            // 인증 시도
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(id, password)
            );

            // SecurityContext 생성 및 설정
            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
            securityContext.setAuthentication(authentication);
            SecurityContextHolder.setContext(securityContext);

            // 세션에 SecurityContext를 명시적으로 저장
            httpSession.setAttribute(
                    HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    securityContext
            );

            return getUserPort.findUserById(id)
                    .orElseThrow(UserNotFoundException::new);

        } catch (BadCredentialsException e) {
            throw new LoginFailedException();
        } catch (AuthenticationException e) {
            throw new InvalidCredentialsException();
        }
    }
}
