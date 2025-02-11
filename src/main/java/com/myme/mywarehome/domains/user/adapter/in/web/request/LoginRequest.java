package com.myme.mywarehome.domains.user.adapter.in.web.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank(message = "ID는 필수입니다.")
        @Size(min = 6, max = 12, message = "아이디는 6자 이상 12자 이하여야 합니다.")
        String id,

        @NotBlank(message = "비밀번호는 필수입니다.")
        String password
) {
}
