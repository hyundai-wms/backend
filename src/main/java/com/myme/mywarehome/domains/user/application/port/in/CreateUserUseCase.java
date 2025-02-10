package com.myme.mywarehome.domains.user.application.port.in;

import com.myme.mywarehome.domains.user.adapter.in.web.request.CreateUserRequest;
import com.myme.mywarehome.domains.user.adapter.in.web.response.CreateUserResponse;

public interface CreateUserUseCase {
    CreateUserResponse create(CreateUserRequest user);
}
