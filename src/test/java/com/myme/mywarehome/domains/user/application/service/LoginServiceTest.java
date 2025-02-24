package com.myme.mywarehome.domains.user.application.service;

import com.myme.mywarehome.domains.user.application.domain.Role;
import com.myme.mywarehome.domains.user.application.domain.User;
import com.myme.mywarehome.domains.user.application.exception.InvalidCredentialsException;
import com.myme.mywarehome.domains.user.application.exception.LoginFailedException;
import com.myme.mywarehome.domains.user.application.exception.UserNotFoundException;
import com.myme.mywarehome.domains.user.application.port.out.GetUserPort;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import jakarta.servlet.http.HttpSession;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private GetUserPort getUserPort;
    @Mock
    private HttpSession httpSession;
    @Mock
    private Authentication authentication;

    private LoginService loginService;
    private User testUser;

    @BeforeEach
    void setUp() {
        loginService = new LoginService(
                authenticationManager,
                getUserPort,
                httpSession
        );

        testUser = User.builder()
                .id("testId")
                .name("테스트")
                .phoneNumber("01012345678")
                .role(Role.ROLE_WMS_MANAGER)
                .build();

        // SecurityContext 초기화
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("올바른 인증 정보로 로그인에 성공한다")
    void login_withValidCredentials_returnsUser() {
        // given
        String id = "testId";
        String password = "password";

        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .willReturn(authentication);
        given(getUserPort.findUserById(id)).willReturn(Optional.of(testUser));

        // when
        User result = loginService.login(id, password);

        // then
        assertThat(result).isEqualTo(testUser);

        // SecurityContext 검증
        assertThat(SecurityContextHolder.getContext().getAuthentication())
                .isEqualTo(authentication);

        // HttpSession 검증
        verify(httpSession).setAttribute(
                eq(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY),
                any(SecurityContext.class)
        );
    }

    @Test
    @DisplayName("잘못된 비밀번호로 로그인 시도시 예외가 발생한다")
    void login_withInvalidPassword_throwsLoginFailedException() {
        // given
        String id = "testId";
        String wrongPassword = "wrongPassword";

        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .willThrow(new BadCredentialsException("Invalid credentials"));

        // when & then
        assertThatThrownBy(() -> loginService.login(id, wrongPassword))
                .isInstanceOf(LoginFailedException.class);

        assertThat(SecurityContextHolder.getContext().getAuthentication())
                .isNull();
    }

    @Test
    @DisplayName("인증 과정에서 예외 발생시 InvalidCredentialsException이 발생한다")
    void login_withAuthenticationException_throwsInvalidCredentialsException() {
        // given
        String id = "testId";
        String password = "password";

        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .willThrow(new AuthenticationException("Authentication failed") {});

        // when & then
        assertThatThrownBy(() -> loginService.login(id, password))
                .isInstanceOf(InvalidCredentialsException.class);

        assertThat(SecurityContextHolder.getContext().getAuthentication())
                .isNull();
    }

    @Test
    @DisplayName("존재하지 않는 사용자로 로그인 시도시 예외가 발생한다")
    void login_withNonExistentUser_throwsUserNotFoundException() {
        // given
        String id = "nonExistentId";
        String password = "password";

        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .willReturn(authentication);
        given(getUserPort.findUserById(id)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> loginService.login(id, password))
                .isInstanceOf(UserNotFoundException.class);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }
}