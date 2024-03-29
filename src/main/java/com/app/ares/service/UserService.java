package com.app.ares.service;

import com.app.ares.domain.User;
import com.app.ares.dto.UserDTO;
import com.app.ares.form.UpdateForm;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    UserDTO createUser(User user);
    UserDTO getUserByEmail(String email);
    void sendVerificationCode(UserDTO user);
    UserDTO verifyCode(String email, String code);
    void resetPassword(String email);
    UserDTO verifyPasswordKey(String key);
    void updatePassword(Long id, String password, String confirmation);
    UserDTO verifyAccountCode(String code);
    UserDTO updateUserDetails(UpdateForm user, Long id);
    UserDTO getUserById(Long userId);
    void updatePassword(Long id, String currentPassword, String newPassword, String confirmNewPassword);
    void updateUserRole(Long userId, String role);
    void updateAccountSettings(Long userId,Boolean enabled, Boolean notLocked);
    UserDTO toggleMfa(String email);
    void updateImage(UserDTO userDTO, MultipartFile image);
}
