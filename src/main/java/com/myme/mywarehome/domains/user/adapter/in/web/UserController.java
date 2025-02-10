package com.myme.mywarehome.domains.user.adapter.in.web;

import com.myme.mywarehome.domains.user.adapter.in.web.request.CreateUserRequest;
import com.myme.mywarehome.domains.user.adapter.in.web.response.CreateUserResponse;
import com.myme.mywarehome.domains.user.adapter.in.web.response.UserInfoResponse;
import com.myme.mywarehome.domains.user.application.port.in.CreateUserUseCase;
import com.myme.mywarehome.domains.user.application.port.in.GetUserUseCase;
import com.myme.mywarehome.infrastructure.common.response.CommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final GetUserUseCase getUserUseCase;
    private final CreateUserUseCase createUserUseCase;

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
}
