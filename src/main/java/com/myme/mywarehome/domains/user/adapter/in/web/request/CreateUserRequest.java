package com.myme.mywarehome.domains.user.adapter.in.web.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
        @NotBlank(message = "이름은 필수입니다.")
        String name,

        @NotBlank(message = "전화번호는 필수입니다.")
        @Pattern(regexp = "^010\\d{8}$")
        String phoneNumber,

        @NotBlank(message = "아이디는 필수입니다.")
        @Size(min = 4, max = 12, message = "아이디는 4자 이상 20자 이하여야 합니다.")
        String id,

        @NotNull(message = "역할은 필수입니다.")
        @Pattern(regexp = "^(ADMIN|MIDDLE_MANAGER|WMS_MANAGER|WORKER)$", message = "올바른 역할이 아닙니다.")
        String role
) {

}
