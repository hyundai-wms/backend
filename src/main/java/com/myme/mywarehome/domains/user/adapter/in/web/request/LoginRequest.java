package com.myme.mywarehome.domains.user.adapter.in.web.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @Schema(description = "사용자 아이디", example = "admin")
        @NotBlank(message = "ID는 필수입니다.")
        @Size(min = 4, max = 12, message = "아이디는 4자 이상 12자 이하여야 합니다.")
        String id,

        @Schema(description = "사용자 비밀번호", example = "1234")
        @NotBlank(message = "비밀번호는 필수입니다.")
        String password
) {
}
