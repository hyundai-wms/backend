package com.myme.mywarehome.domains.user.adapter.in.web.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record PasswordChangeRequest(
        @NotBlank(message = "기존 비밀번호는 필수입니다.")
        String password,

        @NotBlank(message = "새 비밀번호는 필수입니다.")
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,20}$",
                message = "비밀번호는 영문과 숫자를 포함한 8~20자리여야 합니다."
        )
        String newPassword
) {
}
