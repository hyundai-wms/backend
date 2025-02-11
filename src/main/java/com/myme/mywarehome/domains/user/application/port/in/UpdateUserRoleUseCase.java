package com.myme.mywarehome.domains.user.application.port.in;

import com.myme.mywarehome.domains.user.application.domain.User;
import com.myme.mywarehome.domains.user.application.dto.in.UpdateUserRoleCommand;

public interface UpdateUserRoleUseCase {
    User updateRole(UpdateUserRoleCommand command);
}
