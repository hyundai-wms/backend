package com.myme.mywarehome.domains.user.application.service;

import com.myme.mywarehome.domains.user.application.domain.Role;
import com.myme.mywarehome.domains.user.application.domain.User;
import com.myme.mywarehome.domains.user.application.dto.in.GetAllUserCommand;
import com.myme.mywarehome.domains.user.application.port.out.GetUserPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GetAllUserServiceTest {

    @Mock
    private GetUserPort getUserPort;

    private GetAllUserService getAllUserService;
    private List<User> users;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        getAllUserService = new GetAllUserService(getUserPort);

        users = List.of(
                User.builder()
                        .id("testId1")
                        .name("테스트1")
                        .phoneNumber("01012345678")
                        .role(Role.ROLE_WMS_MANAGER)
                        .build(),
                User.builder()
                        .id("testId2")
                        .name("테스트2")
                        .phoneNumber("01087654321")
                        .role(Role.ROLE_MIDDLE_MANAGER)
                        .build()
        );

        pageable = PageRequest.of(0, 10);
    }

    @Test
    @DisplayName("검색 조건 없이 모든 사용자를 조회한다")
    void getAllUsers_withNoSearchCondition_returnsAllUsers() {
        // given
        GetAllUserCommand command = new GetAllUserCommand(null, null, null, null);
        Page<User> expectedPage = new PageImpl<>(users, pageable, users.size());

        given(getUserPort.findAllUsers(null, null, null, null, pageable))
                .willReturn(expectedPage);

        // when
        Page<User> result = getAllUserService.getAllUsers(command, pageable);

        // then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).isEqualTo(users);
        verify(getUserPort).findAllUsers(null, null, null, null, pageable);
    }

    @Test
    @DisplayName("이름으로 사용자를 검색한다")
    void getAllUsers_withNameCondition_returnsFilteredUsers() {
        // given
        String searchName = "테스트1";
        GetAllUserCommand command = new GetAllUserCommand(searchName, null, null, null);
        Page<User> expectedPage = new PageImpl<>(List.of(users.get(0)), pageable, 1);

        given(getUserPort.findAllUsers(eq(searchName), any(), any(), any(), eq(pageable)))
                .willReturn(expectedPage);

        // when
        Page<User> result = getAllUserService.getAllUsers(command, pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo(searchName);
        verify(getUserPort).findAllUsers(searchName, null, null, null, pageable);
    }

    @Test
    @DisplayName("모든 검색 조건으로 사용자를 검색한다")
    void getAllUsers_withAllConditions_returnsFilteredUsers() {
        // given
        GetAllUserCommand command = new GetAllUserCommand(
                "테스트1",
                "testId1",
                "01012345678",
                Role.ROLE_WMS_MANAGER
        );
        Page<User> expectedPage = new PageImpl<>(List.of(users.get(0)), pageable, 1);

        given(getUserPort.findAllUsers(
                eq(command.name()),
                eq(command.id()),
                eq(command.phoneNumber()),
                eq(command.role()),
                eq(pageable)
        )).willReturn(expectedPage);

        // when
        Page<User> result = getAllUserService.getAllUsers(command, pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(command.id());
        assertThat(result.getContent().get(0).getName()).isEqualTo(command.name());
        assertThat(result.getContent().get(0).getPhoneNumber()).isEqualTo(command.phoneNumber());
        assertThat(result.getContent().get(0).getRole()).isEqualTo(command.role());

        verify(getUserPort).findAllUsers(
                command.name(),
                command.id(),
                command.phoneNumber(),
                command.role(),
                pageable
        );
    }
}