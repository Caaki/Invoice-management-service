package com.app.ares.service;

import com.app.ares.domain.User;
import com.app.ares.dto.UserDTO;
import com.app.ares.form.UpdateForm;

public interface UserService {

    UserDTO createUser(User user);
    UserDTO getUserByEmail(String email);
    void sendVerificationCode(UserDTO user);
    UserDTO verifyCode(String email, String code);
    void resetPassword(String email);
    UserDTO verifyPasswordKey(String key);
    void renewPassword(String key, String password, String confirmation);
    UserDTO verifyAccountCode(String code);
    UserDTO updateUserDetails(UpdateForm user);
    UserDTO getUserById(Long userId);
    void updatePassword(Long id, String currentPassword, String newPassword, String confirmNewPassword);
    void updateUserRole(Long userId, String role);
    void updateAccountSettings(Long userId,Boolean enabled, Boolean notLocked);
}
