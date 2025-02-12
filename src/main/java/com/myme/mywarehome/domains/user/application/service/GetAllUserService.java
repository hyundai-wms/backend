package com.myme.mywarehome.domains.user.application.service;

import com.myme.mywarehome.domains.user.application.domain.User;
import com.myme.mywarehome.domains.user.application.dto.in.GetAllUserCommand;
import com.myme.mywarehome.domains.user.application.port.in.GetAllUserUseCase;
import com.myme.mywarehome.domains.user.application.port.out.GetUserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetAllUserService implements GetAllUserUseCase {
    private final GetUserPort getUserPort;

    @Override
    public Page<User> getAllUsers(GetAllUserCommand command, Pageable pageable) {
        return getUserPort.findAllUsers(command.name(), command.id(), command.phoneNumber(), command.role(), pageable);
    }
}
