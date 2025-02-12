package com.myme.mywarehome.domains.user.adapter.in.web;

import com.myme.mywarehome.domains.user.adapter.in.web.request.LoginRequest;
import com.myme.mywarehome.domains.user.adapter.in.web.request.PasswordChangeRequest;
import com.myme.mywarehome.domains.user.adapter.in.web.response.UserInfoResponse;
import com.myme.mywarehome.domains.user.application.port.in.LoginUseCase;
import com.myme.mywarehome.domains.user.application.port.in.UpdateUserPasswordUseCase;
import com.myme.mywarehome.infrastructure.common.response.CommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final LoginUseCase loginUseCase;
    private final UpdateUserPasswordUseCase updateUserPasswordUseCase;

    @PostMapping("/login")
    public CommonResponse<UserInfoResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return CommonResponse.from(
                UserInfoResponse.from(loginUseCase.login(loginRequest.id(), loginRequest.password()))
        );
    }

    @PatchMapping("/password")
    public CommonResponse<Void> updatePassword(@Valid @RequestBody PasswordChangeRequest passwordChangeRequest) {
        updateUserPasswordUseCase.updatePassword(passwordChangeRequest.password(), passwordChangeRequest.newPassword());
        return CommonResponse.empty();
    }
}
