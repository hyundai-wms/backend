package com.myme.mywarehome.domains.user.application.port.in;

public interface UpdateUserPasswordUseCase {
    void updatePassword(String oldPassword, String newPassword);
}
