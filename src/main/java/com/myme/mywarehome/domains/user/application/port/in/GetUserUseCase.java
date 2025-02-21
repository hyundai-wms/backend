package com.myme.mywarehome.domains.user.application.port.in;

import com.myme.mywarehome.domains.user.application.domain.Role;
import com.myme.mywarehome.domains.user.application.domain.User;
import java.util.List;
import java.util.Optional;

public interface GetUserUseCase {
    User getUser();
    List<User> findAllByRole(Role role);
}
