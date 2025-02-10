package com.myme.mywarehome.domains.user.adapter.in.web;

import com.myme.mywarehome.domains.user.adapter.in.web.request.CreateUserRequest;
import com.myme.mywarehome.domains.user.adapter.in.web.response.CreateUserResponse;
import com.myme.mywarehome.domains.user.application.port.in.CreateUserUseCase;
import com.myme.mywarehome.infrastructure.common.response.CommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final CreateUserUseCase createUserUseCase;

    @PostMapping
    public CommonResponse<CreateUserResponse> create(@Valid @RequestBody CreateUserRequest userCreateRequest) {
        return CommonResponse.from(createUserUseCase.create(userCreateRequest));
    }
}
