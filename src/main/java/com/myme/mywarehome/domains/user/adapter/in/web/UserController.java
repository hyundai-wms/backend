package com.myme.mywarehome.domains.user.adapter.in.web;

import com.myme.mywarehome.domains.user.adapter.in.web.request.CreateUserRequest;
import com.myme.mywarehome.domains.user.adapter.in.web.request.UpdateUserRoleRequest;
import com.myme.mywarehome.domains.user.adapter.in.web.response.CreateUserResponse;
import com.myme.mywarehome.domains.user.adapter.in.web.response.UserInfoResponse;
import com.myme.mywarehome.domains.user.application.dto.in.UpdateUserRoleCommand;
import com.myme.mywarehome.domains.user.application.port.in.CreateUserUseCase;
import com.myme.mywarehome.domains.user.application.port.in.GetUserUseCase;
import com.myme.mywarehome.domains.user.application.port.in.UpdateUserRoleUseCase;
import com.myme.mywarehome.infrastructure.common.response.CommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final GetUserUseCase getUserUseCase;
    private final CreateUserUseCase createUserUseCase;
    private final UpdateUserRoleUseCase updateUserRoleUseCase;

    @GetMapping("/me")
    public CommonResponse<UserInfoResponse> getCurrentUser(){
        return CommonResponse.from(
                UserInfoResponse.of(getUserUseCase.getUser())
        );
    }

    @PostMapping
    public CommonResponse<CreateUserResponse> create(@Valid @RequestBody CreateUserRequest userCreateRequest) {
        return CommonResponse.from(
                CreateUserResponse.of(createUserUseCase.create(userCreateRequest.toEntity()))
        );
    }

    @PatchMapping("/{userId}/roles")
    public CommonResponse<UserInfoResponse> updateUserRoles(@PathVariable("userId") Long userId, @Valid @RequestBody UpdateUserRoleRequest updateUserRoleRequest) {
        return CommonResponse.from(
                UserInfoResponse.of(updateUserRoleUseCase.updateRole(UpdateUserRoleCommand.of(userId, updateUserRoleRequest.role())))
        );
    }
}
