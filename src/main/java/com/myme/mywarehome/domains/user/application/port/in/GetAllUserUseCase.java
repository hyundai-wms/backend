package com.myme.mywarehome.domains.user.application.port.in;

import com.myme.mywarehome.domains.user.application.domain.User;
import com.myme.mywarehome.domains.user.application.dto.in.GetAllUserCommand;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GetAllUserUseCase {
    Page<User> getAllUsers(GetAllUserCommand command, Pageable pageable);
}
