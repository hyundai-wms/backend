package com.myme.mywarehome.domains.user.adapter.in.web;

import com.myme.mywarehome.domains.user.adapter.in.web.request.LoginRequest;
import com.myme.mywarehome.domains.user.adapter.in.web.response.UserInfoResponse;
import com.myme.mywarehome.domains.user.application.port.in.LoginUseCase;
import com.myme.mywarehome.infrastructure.common.response.CommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final LoginUseCase loginUseCase;

    @PostMapping("/login")
    public CommonResponse<UserInfoResponse> login(@Valid @RequestBody LoginRequest loginRequest){
        return CommonResponse.from(
                UserInfoResponse.of(loginUseCase.login(loginRequest.id(), loginRequest.password()))
        );
    }
}
