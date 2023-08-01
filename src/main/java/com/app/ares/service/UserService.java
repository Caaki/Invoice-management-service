package com.app.ares.service;

import com.app.ares.domain.User;
import com.app.ares.dto.UserDTO;

public interface UserService {

    UserDTO createUser(User user);
    UserDTO getUserByEmail(String email);
    void sendVerificationCode(UserDTO user);
    UserDTO verifyCode(String email, String code);
    void resetPassword(String email);
    UserDTO verifyPasswordKey(String key);
    void renewPassword(String key, String password, String confirmation);
}
